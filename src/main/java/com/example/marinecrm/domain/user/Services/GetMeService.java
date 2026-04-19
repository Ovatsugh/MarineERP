package com.example.marinecrm.domain.user.Services;

import com.example.marinecrm.Query;
import com.example.marinecrm.domain.user.User;
import com.example.marinecrm.domain.user.DTO.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetMeService implements Query<Void, UserResponse> {

    @Override
    @Transactional(readOnly = true)
    public UserResponse execute(Void input) {
        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new UserResponse(authenticatedUser);
    }
}
