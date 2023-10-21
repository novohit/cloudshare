package com.cloudshare.server;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.cloudshare.server.user.model.User;
import com.cloudshare.server.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author novo
 * @since 2023/10/21
 */
@Component
public class DataGenerationRunner implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataGenerationRunner(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        User root = genUser("root", "root");
        if (userRepository.findByUsername(root.getUsername()).isEmpty()) {
            userRepository.save(root);
        }
        User guest = genUser("guest", "guest");
        if (userRepository.findByUsername(guest.getUsername()).isEmpty()) {
            userRepository.save(guest);
        }
    }

    private User genUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        String salt = RandomUtil.randomString(8);
        String cryptPassword = SecureUtil.md5(salt + password);
        user.setSalt(salt);
        user.setPassword(cryptPassword);
        user.setAvatar("https://zwx-images-1305338888.cos.ap-guangzhou.myqcloud.com/common/avatar-2023.jpg");
        return user;
    }
}
