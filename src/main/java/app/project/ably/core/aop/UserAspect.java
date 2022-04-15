package app.project.ably.core.aop;

import app.project.ably.user.dto.UserDTO;
import app.project.ably.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class UserAspect {

    private final UserService userService;

    @Around("execution(* *(.., @User (*), ..))")
    public Object coverAroundUserAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // User 정보 반환
        Object[] args = Arrays.stream(joinPoint.getArgs()).map(data -> {
            if (data instanceof UserDTO) {
                data = userService.getUserByToken(request);
            }
            return data;
        }).toArray();

        return joinPoint.proceed(args);
    }
}
