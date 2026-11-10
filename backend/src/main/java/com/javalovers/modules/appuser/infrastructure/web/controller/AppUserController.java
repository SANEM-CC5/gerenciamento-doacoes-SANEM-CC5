package com.javalovers.modules.appuser.infrastructure.web.controller;

import com.javalovers.shared.utils.HttpUtils;
import com.javalovers.modules.appuser.application.dto.request.AppUserFormDTO;
import com.javalovers.modules.appuser.application.dto.request.AppUserUpdateDTO;
import com.javalovers.modules.appuser.application.dto.response.AppUserDTO;
import com.javalovers.modules.appuser.domain.entity.AppUser;
import com.javalovers.modules.appuser.application.service.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class AppUserController {

    private final AppUserService userService;

    @GetMapping
    public ResponseEntity<Page<AppUserDTO>> listPaged(Pageable pageable) {
        Page<AppUser> userPage = userService.list(pageable);
        Page<AppUserDTO> userDTOPage = userService.generateUserDTOPage(userPage);

        return ResponseEntity.ok(userDTOPage);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AppUserDTO>> list() {
        List<AppUser> userList = userService.list();
        List<AppUserDTO> userDTOList = userService.generateUserDTOList(userList);

        return ResponseEntity.ok(userDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUserDTO> get(@PathVariable Long id) {
        AppUser user = userService.getOrNull(id);
        if(user == null) return ResponseEntity.notFound().build();

        AppUserDTO userDTO = userService.generateUserDTO(user);

        return ResponseEntity.ok(userDTO);
    }

    @PostMapping
    public ResponseEntity<AppUserDTO> create(@RequestBody @Valid AppUserFormDTO appUserFormDTO, UriComponentsBuilder uriComponentsBuilder) {
        AppUser user = userService.generateUser(appUserFormDTO);
        userService.save(user);

        URI uri = HttpUtils.createURI(uriComponentsBuilder, "profile", user.getUserId());

        return ResponseEntity.created(uri).body(userService.generateUserDTO(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid AppUserUpdateDTO appUserUpdateDTO) {
        AppUser user = userService.getOrNull(id);
        if(user == null) return ResponseEntity.notFound().build();

        userService.updateUser(user, appUserUpdateDTO);

        userService.save(user);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        AppUser user = userService.getOrNull(id);
        if(user == null) return ResponseEntity.notFound().build();

        userService.delete(user);

        return ResponseEntity.noContent().build();
    }
}
