package com.haha.spring.controller.demo;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/management")
@Tag(name = "Management")
//@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class ManagementController {


    @Operation(
            description = "Get endpoint for manager",
            summary = "This is a summary for management get endpoint",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }

    )
    @GetMapping
//    @PreAuthorize("hasAnyAuthority('admin:read','management:read')")
    public String get() {
        return "GET:: management controller";
    }
    @PostMapping
//    @PreAuthorize("hasAnyAuthority('admin:create','management:create')")
    public String post() {
        return "POST:: management controller";
    }
    @PutMapping
//    @PreAuthorize("hasAnyAuthority('admin:update','management:update')")
    public String put() {
        return "PUT:: management controller";
    }
    @DeleteMapping
//    @PreAuthorize("hasAnyAuthority('admin:delete','management:delete')")
    public String delete() {
        return "DELETE:: management controller";
    }
}