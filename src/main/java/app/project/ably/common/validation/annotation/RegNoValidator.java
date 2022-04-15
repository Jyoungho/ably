package app.project.ably.common.validation.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RegNoValidator implements ConstraintValidator<RegNoValid, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 주민등록번호 유효성 검사
        // 1. 각 자리에 2,3,4,5,6,7,8,9,2,3,4,5 를 곱한다. (단, 마지막 자리는 제외)
        // 2. 각 자리의 숫자를 모두 더한다.
        // 3. 11로 나눈 나머지 값을 구함
        // 4. 11에서 결과값을 뺀다.
        // 5. 결과가 주민등록번호 마지막 자리와 일치하면 유효한 주민등록번호이다.
        if (value == null) return false;

        int[] cal = { 2, 3, 4, 5, 6, 7, 0, 8, 9, 2, 3, 4, 5 };
        int total = 0;

        if (value.length() != 14) return false;

        for (int i = 0; i < cal.length; i++) {
            if (i == 6) continue;
            total += cal[i] * Integer.parseInt(value.substring(i, (i + 1)));
        }

        int result = 11 - total % 11;
        if (result >= 10) result %= 10;

        return result == Integer.parseInt(value.substring(13));
    }
}
