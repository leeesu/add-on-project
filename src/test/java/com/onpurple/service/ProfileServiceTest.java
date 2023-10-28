package com.onpurple.service;

import com.onpurple.dto.request.ProfileUpdateRequestDto;
import com.onpurple.dto.response.ApiResponseDto;
import com.onpurple.dto.response.MessageResponseDto;
import com.onpurple.dto.response.ProfileResponseDto;
import com.onpurple.enums.SuccessCode;
import com.onpurple.model.User;
import com.onpurple.repository.UserRepository;
import com.onpurple.helper.EntityValidatorManager;
import org.junit.jupiter.api.BeforeEach;
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

import static com.onpurple.enums.SuccessCode.SUCCESS_PROFILE_GET_ALL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    EntityValidatorManager entityValidatorManager;

    @InjectMocks
    ProfileService profileService;

    List<User> users;

    @BeforeEach
    public void setup() {
        users = new ArrayList<>();

        // Add some mock user data to the list.
        for (int i = 0; i < 3; i++) {
            User user = mock(User.class);
            users.add(user);
        }
    }

    @Test
    public void testGetAllProfiles() {
        int pageSize = 10;
        long totalUsers = users.size();
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);

        when(userRepository.count()).thenReturn((long) users.size()); // userRepository.count()에 대한 모킹

        for (int i = 0; i < totalPages; ++i) {
            Page<User> userPage = new PageImpl<>(users.subList(i * pageSize, Math.min((i + 1) * pageSize, users.size())));
            when(userRepository.findAll(PageRequest.of(i, pageSize))).thenReturn(userPage); // userRepository.findAll(Pageable pageable)에 대한 모킹
        }

        ApiResponseDto<List<ProfileResponseDto>> response = profileService.getAllProfiles();

        assertNotNull(response);

        assertEquals(SUCCESS_PROFILE_GET_ALL.getMessage(), response.getMessage());

        assertFalse(response.getData().isEmpty());

        // 무작위 페이지에서 가져온 사용자 수와 같아야 함
        assertTrue(response.getData().size() <= pageSize);
    }


    @Test
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