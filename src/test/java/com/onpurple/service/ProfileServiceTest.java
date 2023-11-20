package com.onpurple.service;

import com.onpurple.domain.user.dto.ProfileResponseDto;
import com.onpurple.domain.user.dto.ProfileUpdateRequestDto;
import com.onpurple.domain.user.service.ProfileService;
import com.onpurple.domain.user.model.User;
import com.onpurple.domain.user.repository.UserRepository;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.enums.SuccessCode;
import com.onpurple.global.helper.EntityValidatorManager;
import com.onpurple.global.redis.cacheRepository.CountCacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.onpurple.global.enums.SuccessCode.SUCCESS_PROFILE_GET_ALL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    EntityValidatorManager entityValidatorManager;

    @Mock
    CountCacheRepository countCacheRepository;

    ProfileService profileService;

    List<User> users;

    @BeforeEach
    public void setup() {
        users = new ArrayList<>();

        // Add some mock user data to the list.
        for (int i = 0; i < 33; i++) {
            User user = mock(User.class);
            users.add(user);
        }

        profileService = new ProfileService(userRepository, entityValidatorManager, countCacheRepository);
    }

    // 테스트 메소드 등 나머지 코드는 동일하게 유지합니다.


    @Test
    @DisplayName("전체 프로필 조회")
    public void testGetAllProfiles() {
        int pageSize = 10;
        long totalUsers = 100;

        List<User> users = new ArrayList<>();
        for (int i = 0; i < totalUsers; i++) {
            User user = new User();
            users.add(user);
        }

        when(countCacheRepository.getCount()).thenReturn(Optional.of(String.valueOf(totalUsers)));

        for (int i = 0; i < totalUsers / pageSize; i++) {
            int start = i * pageSize;
            int end = Math.min((i + 1) * pageSize, users.size());
            lenient().when(userRepository.findAll(PageRequest.of(i, pageSize)))
                    .thenReturn(new PageImpl<>(users.subList(start, end)));
        }

        ApiResponseDto<List<ProfileResponseDto>> response = profileService.getAllProfiles();

        assertNotNull(response);
        assertEquals(SUCCESS_PROFILE_GET_ALL.getMessage(), response.getMessage());
        assertFalse(response.getData().isEmpty());
        assertTrue(response.getData().size() <= pageSize);
    }


    @Test
    @DisplayName("상세 프로필 조회")
    void getProfile() {
        User user = User.builder()
                .username("회원명")
                .password("1234455667")
                .imageUrl("imgUrl")
                .likes(0)
                .age(22)
                .gender("성골")
                .area("신라")
                .build();
        given(entityValidatorManager.validateProfile(any())).willReturn(user);

        ApiResponseDto<ProfileResponseDto> response = profileService.getProfile(user.getId());
        assertEquals(response.getData().getAge(), user.getAge());
    }

    @Test
    @DisplayName("프로필 수정")
    void updateProfile() {
        User user = mock(User.class);
        ProfileUpdateRequestDto requestDto = ProfileUpdateRequestDto.builder()
                .area("고구려")
                .drink("음주합니다")
                .job("직업")
                .build();
        ApiResponseDto<MessageResponseDto> response = profileService.updateProfile(requestDto, user);

        assertEquals(SuccessCode.SUCCESS_PROFILE_EDIT.getMessage(),response.getMessage());

    }
}