package com.javalovers.modules.donation.infrastructure.web.controller;

import com.javalovers.modules.donation.application.dto.request.DonationFormDTO;
import com.javalovers.modules.donation.application.dto.response.DonationDTO;
import com.javalovers.modules.donation.application.service.DonationService;
import com.javalovers.modules.donor.domain.entity.Donor;
import com.javalovers.modules.donor.application.service.DonorService;
import com.javalovers.modules.appuser.domain.entity.AppUser;
import com.javalovers.modules.appuser.application.service.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/donation")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;
    private final DonorService donorService;
    private final AppUserService userService;

    @PostMapping({"", "/"})
    public ResponseEntity<DonationDTO> create(@Valid @RequestBody DonationFormDTO formDTO) {
        Donor donor = donorService.getOrThrowException(formDTO.donorId());
        AppUser user = userService.getOrThrowException(formDTO.attendantUserId());

        DonationDTO donationCriada = donationService.create(formDTO, donor, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(donationCriada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonationDTO> findById(@PathVariable Long id) {
        DonationDTO donation = donationService.findById(id);

        return ResponseEntity.ok(donation);
    }

    @GetMapping({"", "/"})
    public ResponseEntity<List<DonationDTO>> findAll(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false) String donorName,
            @RequestParam(required = false) String attendantName,
            @RequestParam(required = false) String itemName
    ) {
        List<DonationDTO> donations = donationService.findAllFiltered(
                startDate, endDate, donorName, attendantName, itemName
        );

        return ResponseEntity.ok(donations);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        donationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
