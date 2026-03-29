package com.example.marinecrm.domain.user.Services;

import com.example.marinecrm.Query;
import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.domain.user.UserRepository;
import com.example.marinecrm.domain.user.DTO.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAllUserService implements Query<Pageable, Page<UserResponse>> {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> execute(Pageable pageable) {
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userRepository.findByCompany_Id(authenticatedUser.getCompany().getId(), pageable).map(UserResponse::new);
    }
}
