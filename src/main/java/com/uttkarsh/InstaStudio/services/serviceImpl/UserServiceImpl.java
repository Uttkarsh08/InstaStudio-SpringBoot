package com.uttkarsh.InstaStudio.services.serviceImpl;

import com.uttkarsh.InstaStudio.dto.user.UserRequestDTO;
import com.uttkarsh.InstaStudio.dto.user.UserResponseDTO;
import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.exceptions.EventAlreadyAddedException;
import com.uttkarsh.InstaStudio.exceptions.ResourceNotFoundException;
import com.uttkarsh.InstaStudio.repositories.UserRepository;
import com.uttkarsh.InstaStudio.services.UserService;
import com.uttkarsh.InstaStudio.utils.mappers.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User getUserByFirebaseId(String firebaseId){
        return userRepository.getUserByFirebaseId(firebaseId)
                .orElse(null);
    }

    public UserResponseDTO createUser(UserRequestDTO requestDTO) {

        if(existsByFirebaseId(requestDTO.getFirebaseId())){
            throw new EventAlreadyAddedException("User Already exists");
        }
        if(requestDTO.getUserType() == null){
            throw new ResourceNotFoundException("UserType can't be null");
        }

        User user = new User();
        user.setFirebaseId(requestDTO.getFirebaseId());
        user.setUserName(requestDTO.getUserName());
        user.setUserPhoneNo(requestDTO.getUserPhoneNo());
        user.setUserEmail(requestDTO.getUserEmail());
        user.setUserType(requestDTO.getUserType());
        User savedUser = userRepository.save(user);
        return userMapper.toEventDTO(savedUser);
    }

    public boolean existsByFirebaseId(String firebaseId) {
        return userRepository.existsByFirebaseId(firebaseId);
    }
}
