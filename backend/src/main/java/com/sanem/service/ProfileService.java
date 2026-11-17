package com.sanem.service;

import com.sanem.specification.SearchCriteria;
import com.sanem.specification.SpecificationHelper;
import com.sanem.model.dto.ProfileFilterDTO;
import com.sanem.model.dto.ProfileFormDTO;
import com.sanem.model.dto.ProfileDTO;
import com.sanem.model.entity.Profile;
import com.sanem.mapper.ProfileCreateMapper;
import com.sanem.mapper.ProfileDTOMapper;
import com.sanem.mapper.ProfileUpdateMapper;
import com.sanem.repository.ProfileRepository;
import com.sanem.specification.ProfileSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileCreateMapper profileCreateMapper;
    private final ProfileDTOMapper profileDTOMapper;
    private final ProfileUpdateMapper profileUpdateMapper;

    public Profile generateProfile(ProfileFormDTO profileFormDTO) {
        return profileCreateMapper.convert(profileFormDTO);
    }

    public void save (Profile profile) {
        profileRepository.save(profile);
    }

    public ProfileDTO generateProfileDTO(Profile profile) {
        return profileDTOMapper.convert(profile);
    }

    public Profile getOrNull(Long id){
        return profileRepository.findById(id).orElse(null);
    }

    public void updateProfile(Profile profile, ProfileFormDTO profileFormDTO) {
        profileUpdateMapper.update(profile, profileFormDTO);
    }

    public void delete(Profile profile) {
        profileRepository.delete(profile);
    }

    public List<Profile> list(ProfileFilterDTO profileFilterDTO) {
        Specification<Profile> profileSpecification = generateSpecification(profileFilterDTO);
        return profileRepository.findAll(profileSpecification);
    }

    public Page<Profile> list(Pageable pageable, ProfileFilterDTO profileFilterDTO) {
        Specification<Profile> profileSpecification = generateSpecification(profileFilterDTO);
        return profileRepository.findAll(profileSpecification, pageable);
    }

    private Specification<Profile> generateSpecification(ProfileFilterDTO profileFilterDTO) {
        SearchCriteria<String> descriptionCriteria = SpecificationHelper.generateInnerLikeCriteria("description", profileFilterDTO.description());
        SearchCriteria<String> nameCriteria = SpecificationHelper.generateInnerLikeCriteria("name", profileFilterDTO.name());

        Specification<Profile> descriptionSpecification = new ProfileSpecification(descriptionCriteria);
        Specification<Profile> nameSpecification = new ProfileSpecification(nameCriteria);

        return Specification.where(descriptionSpecification)
                .and(nameSpecification);
    }

    public Page<ProfileDTO> generateProfileDTOPage(Page<Profile> profilePage) {
        return profilePage.map(this::generateProfileDTO);
    }

    public List<ProfileDTO> generateProfileDTOList(List<Profile> profileList) {
        return profileList.stream().map(profileDTOMapper::convert).toList();
    }

//    public Profile getOrThrowException(Long id) {
//        return profileRepository.findById(id).orElseThrow(
//                () -> new EntityNotFoundException("Profile", id)
//        );
//    }

}
