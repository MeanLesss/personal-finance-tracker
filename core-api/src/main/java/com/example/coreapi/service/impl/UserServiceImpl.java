package com.example.coreapi.service.impl;

import com.example.common.entity.User;
import com.example.common.mappers.UserMapper;
import com.example.common.mappers.responses.UserView;
import com.example.common.payload.request.UserRequest;
import com.example.common.repository.UserRepository;
import com.example.common.util.Pagination;
import com.example.coreapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserView save(UserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        return userMapper.mapFrom(userRepository.save(user));
    }

    @Override
    public UserView findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.mapFrom(user);
    }

    @Override
    public List<UserView> findAll(Pagination pagination) {
        Page<User> page = userRepository.findAll(pagination.getJPAPageRequest());
        pagination.setTotalCounts(page.getTotalElements());
        return page.getContent().stream()
                .map(userMapper::mapFromForList)
                .toList();
    }

    @Override
    public UserView update(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        return userMapper.mapFrom(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
    }

}