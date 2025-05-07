package com.uttkarsh.InstaStudio.services.serviceImpl;

import com.uttkarsh.InstaStudio.dto.user.UserRequestDTO;
import com.uttkarsh.InstaStudio.entities.User;
import com.uttkarsh.InstaStudio.entities.enums.UserType;
import com.uttkarsh.InstaStudio.exceptions.EventAlreadyAddedException;
import com.uttkarsh.InstaStudio.exceptions.EventAlreadyAssignedException;
import com.uttkarsh.InstaStudio.exceptions.ResourceNotFoundException;
import com.uttkarsh.InstaStudio.repositories.UserRepository;
import com.uttkarsh.InstaStudio.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public User getUserByFirebaseId(String firebaseId){
        return userRepository.getUserByFirebaseId(firebaseId)
                .orElse(null);
    }

    public User createUser(UserRequestDTO requestDTO) {

        if(existsByFirebaseId(requestDTO.getFirebaseId())){
            throw new EventAlreadyAddedException("User Already exists");
        }

        User user = new User();
        user.setFirebaseId(requestDTO.getFirebaseId());
        user.setUserName(requestDTO.getUserName());
        user.setUserPhoneNo(requestDTO.getUserPhoneNo());
        user.setUserEmail(requestDTO.getUserEmail());
        user.setUserType(requestDTO.getUserType());
        return userRepository.save(user);
    }

    public boolean existsByFirebaseId(String firebaseId) {
        return userRepository.existsByFirebaseId(firebaseId);
    }
}
