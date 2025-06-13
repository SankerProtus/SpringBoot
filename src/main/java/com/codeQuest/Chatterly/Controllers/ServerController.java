package com.codeQuest.Chatterly.Controllers;

import com.codeQuest.Chatterly.DTOs.CreateServerDto;
import com.codeQuest.Chatterly.Services.ServerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/servers")
@AllArgsConstructor
public class ServerController {
    private final ServerService serverService;

    //    Create a server
    @PostMapping("/server")
    public ResponseEntity<?> createServer(@Valid @RequestBody CreateServerDto createServerDto) {
        return serverService.createServer(createServerDto);
    }

//    Needed to view server details without joining it
    @GetMapping("/{serverId}")
    public ResponseEntity<?> getServer(@PathVariable Long serverId) {
        return serverService.getServer(serverId);
    }

//    When a user wants to join a server
    @PostMapping("/{serverId}/join")
    public ResponseEntity<?> joinServer(@PathVariable Long serverId) {
        return serverService.joinServer(serverId);
    }

//    Delete a server
    @DeleteMapping("/servers/{id}")
    public ResponseEntity<?> deleteServer(@PathVariable Long id) {
        return serverService.deleteServer(id);
    }
}

