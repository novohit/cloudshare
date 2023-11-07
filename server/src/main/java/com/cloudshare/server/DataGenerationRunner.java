package com.cloudshare.server;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.cloudshare.server.common.constant.BizConstant;
import com.cloudshare.server.order.model.Product;
import com.cloudshare.server.order.repository.ProductRepository;
import com.cloudshare.server.user.enums.PlanLevel;
import com.cloudshare.server.user.model.User;
import com.cloudshare.server.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author novo
 * @since 2023/10/21
 */
@Component
public class DataGenerationRunner implements CommandLineRunner {

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    public DataGenerationRunner(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
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
        if (productRepository.findByPlanAndDeletedAtIsNull(PlanLevel.BASE)
                .isEmpty()) {
            productRepository.save(basePlan());
        }
        if (productRepository.findByPlanAndDeletedAtIsNull(PlanLevel.PLUS)
                .isEmpty()) {
            productRepository.save(plusPlan());
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
        user.setPlan(PlanLevel.FREE);
        user.setTotalQuota(BizConstant.FREE_PLAN_QUOTA);
        user.setUsedQuota(0L);
        return user;
    }

    private Product basePlan() {
        Product product = new Product();
        product.setTitle("Base Plan");
        product.setDetail("10GB空间");
        product.setPlan(PlanLevel.BASE);
        product.setAmount(BigDecimal.valueOf(9.9));
        return product;
    }

    private Product plusPlan() {
        Product product = new Product();
        product.setTitle("Plus Plan");
        product.setDetail("100GB空间");
        product.setPlan(PlanLevel.PLUS);
        product.setAmount(BigDecimal.valueOf(19.9));
        return product;
    }
}
