package org.example.tpuserapi.service;

import org.example.tpuserapi.dto.UserDto;
import org.example.tpuserapi.entity.User;
import org.example.tpuserapi.exception.EmailAlreadyExistsException;
import org.example.tpuserapi.exception.ObjectNotFoundException;
import org.example.tpuserapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Utilisateur non trouvé avec l'id : " + id));
        return convertToDto(user);
    }

    @Override
    public UserDto createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Un utilisateur avec cet email existe déjà");
        }
        return convertToDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Utilisateur non trouvé avec l'id : " + id));

        if (!existingUser.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("L'email appartient déjà à un autre utilisateur");
        }

        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());

        return convertToDto(userRepository.save(existingUser));
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ObjectNotFoundException("Utilisateur non trouvé avec l'id : " + id);
        }
        userRepository.deleteById(id);
    }

    private UserDto convertToDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    private User convertToEntity(UserDto dto, String password) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(password);
        return user;
    }
}