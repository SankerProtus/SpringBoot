package com.codeQuest.Chatterly.Services;

import com.codeQuest.Chatterly.DTOs.CreateServerDto;
import com.codeQuest.Chatterly.Entities.*;
import com.codeQuest.Chatterly.Enums.ChannelType;
import com.codeQuest.Chatterly.Enums.ServerPermission;
import com.codeQuest.Chatterly.Exception.ResourceNotFoundException;
import com.codeQuest.Chatterly.Repositories.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ServerService {
    private final ServerRepository serverRepository;
    private final CategoryRepository categoryRepository;
    private final ChannelRepository channelRepository;
    private final ServerRoleRepository serverRoleRepository;
    private final ServerMemberRepository serverMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity<?> createServer(CreateServerDto createServerDto) {
        try {
            System.out.println("Creating server with DTO: " + createServerDto);

            Optional<User> owner = userRepository.findById(createServerDto.getOwnerId());
            if (owner.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Server details cannot be null");
            }

            if (createServerDto.getName() == null || createServerDto.getName().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Server name is required");
            }

            Servers newServer = new Servers();
            newServer.setName(createServerDto.getName() != null ?
                    createServerDto.getName() : "New Server");
            newServer.setOwner(owner.get());
            newServer.setCreatedAt(LocalDateTime.now());
            newServer.setServerIcon(createServerDto.getServerIcon());
            newServer.setDescription(createServerDto.getDescription());
            Servers savedServer = serverRepository.save(newServer);

            // Create and save a category
            Category generalCategory = new Category();
            generalCategory.setName("Text Channels");
            generalCategory.setServer(savedServer);
            generalCategory.setPosition(0);
            Category savedCategory = categoryRepository.save(generalCategory);

            // Create and save channels
            Channel generalChannel = new Channel();
            generalChannel.setName("general");
            generalChannel.setType(ChannelType.TEXT);
            generalChannel.setCategory(savedCategory);
            generalChannel.setServer(savedServer);
            generalChannel.setPosition(0);
            channelRepository.save(generalChannel);

            Channel voiceChannel = new Channel();
            voiceChannel.setName("General Voice");
            voiceChannel.setType(ChannelType.VOICE);
            voiceChannel.setCategory(savedCategory);
            voiceChannel.setServer(savedServer);
            voiceChannel.setPosition(1);
            channelRepository.save(voiceChannel);

            // Create and save a role
            ServerRole everyoneRole = serverRoleRepository.findByServerAndName(savedServer, "@everyone")
                    .orElseGet(() -> {
                        ServerRole role = createDefaultRole(savedServer, "@everyone", "#99AAB5");
                        return serverRoleRepository.save(role);
                    });

            ServerRole adminRole = serverRoleRepository.findByServerAndName(savedServer, "Admin")
                    .orElseGet(() -> {
                        ServerRole role = createDefaultRole(savedServer, "Admin", "#E91E63");
                        return serverRoleRepository.save(role);
                    });

            // Create and save server member
            ServerMember ownerMember = new ServerMember();
            ownerMember.setUser(owner.get());
            ownerMember.setServer(savedServer);
            ownerMember.setJoinedAt(LocalDateTime.now());
            ownerMember.setRoles(new HashSet<>(Arrays.asList(everyoneRole, adminRole)));
            serverMemberRepository.save(ownerMember);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createServerResponse(savedServer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server creation failed: " + e.getMessage());
        }
    }

    private ServerRole createDefaultRole(Servers server, String name, String color) {
        ServerRole role = new ServerRole();
        role.setName(name);
        role.setServer(server);
        role.setColor(color);

        Set<String> permissions = new HashSet<>();
        if (name.equals("Admin")) {
            permissions.addAll(Arrays.stream(ServerPermission.values())
                    .map(Enum::name)
                    .collect(Collectors.toSet()));
        } else {
            // Default permissions for @everyone
            permissions.add(ServerPermission.READ_MESSAGES.name());
            permissions.add(ServerPermission.SEND_MESSAGES.name());
            permissions.add(ServerPermission.CONNECT_VOICE.name());
            permissions.add(ServerPermission.SPEAK.name());
        }
        role.setPermissions(permissions);

        return role;
    }

    private Map<String, Object> createServerResponse(Servers server) {
        Map<String, Object> response = new HashMap<>();
        
        // Add null checks
        response.put("id", server.getId());
        response.put("name", server.getName());
        response.put("description", server.getDescription());
        
        if (server.getOwner() != null) {
            response.put("owner", Map.of(
                "id", server.getOwner().getId(),
                "username", server.getOwner().getUsername()
            ));
        }

        List<Map<String, Object>> channels = new ArrayList<>();
        if (server.getChannels() != null) {
            channels = server.getChannels().stream()
            .map(channel -> {
                Map<String, Object> channelMap = new HashMap<>();
                channelMap.put("id", channel.getId());
                channelMap.put("name", channel.getName());
                channelMap.put("type", channel.getType());
                channelMap.put("position", channel.getPosition());
                return channelMap;
            })
            .collect(Collectors.toList());
        }
        
        response.put("channels", channels);
        System.out.println("Final response map: " + response);
        return response;
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getServer(Long serverId) {
        if (serverId == null || serverId <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid server ID"));
        }

        try {
            Optional<Servers> server = serverRepository.findById(serverId);
            return server.map(servers -> ResponseEntity.ok(createServerResponse(servers))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Server not found")));

        } catch (DataAccessException e) {
            log.error("Database error while fetching server {}: {}", serverId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while fetching the server"));
        } catch (Exception e) {
            log.error("Unexpected error while fetching server {}: {}", serverId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> joinServer(@PathVariable Long serverId) {
        if (serverId == null || serverId <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid server ID"));
        }

        try {
            User currentUser = getCurrentUser();

            Servers server = serverRepository.findById(serverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Server not found"));

            // Check if a user is already a member
            if (serverMemberRepository.existsByServerAndUser(server, currentUser)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "User is already a member of this server"));
            }

            // Add user to server members
            ServerMember member = ServerMember.builder()
                    .server(server)
                    .user(currentUser)
                    .joinedAt(LocalDateTime.now())
                    .build();

            serverMemberRepository.save(member);

            return ResponseEntity.ok(Map.of(
                    "message", "Successfully joined server",
                    "serverName", server.getName(),
                    "serverId", server.getId()
            ));

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error joining server {}: {}", serverId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to join server"));
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
    }


    public ResponseEntity<?> deleteServer(Long serverId, Principal principal) {
        if (serverId == null || serverId <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid server ID"));
        }

        try {
            Optional<Servers> optionalServer = serverRepository.findById(serverId);
            if (optionalServer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Server not found"));
            }

            Servers server = optionalServer.get();

            // Get current user
            String currentUsername = principal.getName();
            Optional<User> userOpt = userRepository.findByUsername(currentUsername);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Unauthorized"));
            }

            User user = userOpt.get();

            // Check if the user is the creator and has an ADMIN role
            boolean isCreator = server.getServerOwner().getId().equals(user.getId());
            boolean isAdmin = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));

            if (!isCreator || !isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Only the admin who created this server can delete it"));
            }

            serverRepository.deleteById(serverId);
            return ResponseEntity.ok("Server deleted successfully");

        } catch (Exception e) {
            log.error("Error deleting server {}: {}", serverId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete server"));
        }
    }

}

