package com.onpurple.service;

import com.onpurple.dto.request.ProfileUpdateRequestDto;
import com.onpurple.dto.response.ApiResponseDto;
import com.onpurple.dto.response.MessageResponseDto;
import com.onpurple.dto.response.ProfileResponseDto;
import com.onpurple.enums.SuccessCode;
import com.onpurple.model.User;
import com.onpurple.repository.UserRepository;
import com.onpurple.external.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    ValidationUtil validationUtil;

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
        when(userRepository.findAll()).thenReturn(users);

        ApiResponseDto<List<ProfileResponseDto>> response = profileService.getAllProfiles();

        verify(userRepository).findAll();

        assertNotNull(response);


        assertEquals(SUCCESS_PROFILE_GET_ALL.getMessage(), response.getMessage());

        assertFalse(response.getData().isEmpty());

        assertEquals(users.size(), response.getData().size());
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
        given(validationUtil.validateProfile(any())).willReturn(user);

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