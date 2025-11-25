package com.project.shop.global.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * .env 파일 로더 설정
 *
 * 프로젝트 루트의 .env 파일을 읽어서 환경변수로 등록
 * application.yml에서 ${VARIABLE_NAME} 형태로 참조 가능
 */
public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            // .env 파일 로드 (프로젝트 루트 디렉토리에서 찾음)
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")  // 프로젝트 루트 디렉토리
                    .ignoreIfMissing()  // .env 파일이 없어도 에러 발생 안 함
                    .load();

            ConfigurableEnvironment environment = applicationContext.getEnvironment();

            // .env의 모든 변수를 Spring Environment에 추가
            Map<String, Object> dotenvProperties = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                dotenvProperties.put(entry.getKey(), entry.getValue());
                System.out.println("[DotEnv] Loaded: " + entry.getKey() + "=***");  // 보안상 값은 출력 안 함
            });

            // PropertySource로 등록
            environment.getPropertySources().addFirst(
                    new MapPropertySource("dotenvProperties", dotenvProperties)
            );

            System.out.println("[DotEnv] .env 파일 로드 완료 (" + dotenvProperties.size() + "개 변수)");

        } catch (Exception e) {
            System.err.println("[DotEnv] .env 파일 로드 실패: " + e.getMessage());
            // .env 파일이 없어도 애플리케이션 실행은 계속됨
        }
    }
}
