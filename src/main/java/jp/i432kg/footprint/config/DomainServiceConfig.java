package jp.i432kg.footprint.config;

import jp.i432kg.footprint.domain.repository.PostRepository;
import jp.i432kg.footprint.domain.repository.ReplyRepository;
import jp.i432kg.footprint.domain.repository.UserRepository;
import jp.i432kg.footprint.domain.service.PostDomainService;
import jp.i432kg.footprint.domain.service.ReplyDomainService;
import jp.i432kg.footprint.domain.service.UserDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ドメインサービスを Spring Bean として登録するための構成クラスです。
 * Domain層をSpringFrameworkに依存させないために、各サービスをBeanとして登録します。
 */
@Configuration
public class DomainServiceConfig {

    @Bean
    public UserDomainService userDomainService(final UserRepository userRepository) {
        return new UserDomainService(userRepository);
    }

    @Bean
    public PostDomainService postDomainService(final PostRepository postRepository) {
        return new PostDomainService(postRepository);
    }

    @Bean
    public ReplyDomainService replyDomainService(final ReplyRepository replyRepository) {
        return new ReplyDomainService(replyRepository);
    }
}
