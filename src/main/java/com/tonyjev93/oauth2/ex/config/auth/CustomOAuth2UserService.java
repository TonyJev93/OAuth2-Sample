package com.tonyjev93.oauth2.ex.config.auth;

import com.tonyjev93.oauth2.ex.config.auth.dto.OAuthAttributes;
import com.tonyjev93.oauth2.ex.config.auth.dto.SessionUser;
import com.tonyjev93.oauth2.ex.domain.user.User;
import com.tonyjev93.oauth2.ex.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("OAuth2User Loader 호출");
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 현재 로그인 진행 중인 서비스를 구분하는 코드
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("registrationId : " + registrationId);

        // OAuth2 로그인 진행 시 키가 되는 필드값, Primary Key와 같은 의미
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        System.out.println("userNameAttributeName : " + userNameAttributeName);

        // OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담을 클래스
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        System.out.println("name : " + (String) oAuth2User.getAttributes().get("name"));
        System.out.println("email : " + (String) oAuth2User.getAttributes().get("email"));
        System.out.println("picture : " + (String) oAuth2User.getAttributes().get("picture"));
        User user = saveOrUpdate(attributes);

        /* User 클래스를 SessionUser로 사용하지 않은 이유 */
        // 세션 저장을 위해 직렬화가 필요 -> Entity 클래스는 질렬화 코드를 넣지 않는 것이 좋음.
        // -> 언제 다른 Entity와 관계가 형성될지 모르며, @OneToMany, @ManyToMany 등 자식 엔티티를 가지고 있다면 직렬화 대상에 자식들까지 포함됨
        // -> 성능 이슈, 부수 효과 발생 확률이 높음
        // 따라서, 직렬화 기능을 가진 세션 Dto를 하나 추가로 만드는 것이 좋은 방법.
        httpSession.setAttribute("user", new SessionUser(user));    // 세션에 사용자 정보를 저장하기 위한 Dto 클래스

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        System.out.println("user save start");
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}
