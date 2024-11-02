package com.itwill.rest.domain;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Basic;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "USERS")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Basic(optional = false)
	@EqualsAndHashCode.Include // username field equals()와 hashCode()를 재정의할 때 사용.
	@NaturalId
	private String userName;
	
    @Basic(optional = false)
    @NaturalId
    private String userId;
    
    @Basic(optional = false)
    private String password; // @NaturalId 제거
	
	@Basic(optional = false)
	@NaturalId
	private String email;
	
	@Basic(optional = false)
	private String nickname;

	private String userProfile;

	private String hintQuestion;

	private String hintAnswer;

	@Builder.Default
	private Integer isActive = 1;

	private LocalDate deactivatedUntil;

	// 권한 설정 하는 부분
	// 스프링 시큐리티 적용 후 사용할 오버라이드 메서드
	
	@Builder.Default // Builder 패턴에서도 null이 아닌 HashSet<> 객체로 초기화 될 수 있도록 하는 설정.
	@ToString.Exclude // ToString 메서드에서 제외
	@ElementCollection(fetch = FetchType.LAZY) // 연관 테이블(member_roles) 사용.
	// @ManyToMany 애너테이션 안씀?
	// ! 그거 대신에 ElementCollection 애너테이션 쓴거임.
	@Enumerated(EnumType.STRING) // DB 테이블에 저장될 때 상수(enum) 이름(문자열)을 사용.
	private Set<UserRole> roles = new HashSet<>();

	// 편의 메서드
	// 유저의 권한을 부여하는 메서드.
	public User addRole(UserRole role) {
		roles.add(role);
		return this;
	}

	// 유저의 권한을 한 개 삭제하는 메서드
	public User removeRole(UserRole role) {
		roles.remove(role);
		return this;
	}

	// 유저의 권한을 삭제하는 메서드
	public User clearRoles() {
		roles.clear(); // Set<>이 가지고 있는 모든 원소를 지움.
		return this;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		// ! 람다표현식을 사용하지 않는 방법
		// ArrayList<GrantedAuthority> authorities = new ArrayList<>();
		// for(MemberRole r : roles) {
		// GrantedAuthority auth = new SimpleGrantedAuthority(r.getAuthority());
		// authorities.add(auth);
		// }

		// ! 람다표현식을 사용한 방법
		List<SimpleGrantedAuthority> authorities = roles.stream().map((r) -> new SimpleGrantedAuthority(r.getAuthority()))
				.toList();

		return authorities;
	}

	@Override
	public String getUsername() {
		return this.userName;
	}
	
    // 비밀번호 검증 메서드
    public boolean checkPassword(String rawPassword) {
        return this.password.equals(rawPassword); // 실제로는 암호화된 비밀번호와 비교해야 함
    }

    // 비밀번호 업데이트 메서드
    public User updatePassword(String newPassword) {
        this.password = newPassword; // 암호화된 비밀번호로 업데이트해야 함
        return this;
    }
	
	// 사용자 프로필 업데이트 메서드
    public User updateProfile(String userProfile) {
        this.userProfile = userProfile;
        return this;
    }
	
	// 사용자 정보 수정 메서드
    public User updateUser(String password, String email, String nickname, String userProfile,
                                  String hintQuestion, String hintAnswer) {
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.userProfile = userProfile;
        this.hintQuestion = hintQuestion;
        this.hintAnswer = hintAnswer;
        return this;
    }
    
    // 사용자 계정 비활성화 메서드
    public User deactivateUser(LocalDate deactivatedUntil) {
        this.isActive = 0;
        this.deactivatedUntil = deactivatedUntil;
        return this;
    }
    
}
