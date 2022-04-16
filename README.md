## 기능구현
1. /api/v1/auth/check/phone-number/message 핸드폰 인증메세지 전송
2. /api/v1/auth/check/phone-number 핸드폰 인증
3. /api/v1/user/signup 회원가입
4. /api/v1/auth/login 로그인
5. /api/v1/auth/token 토큰 갱신
6. /api/v1/user/info 내 정보 보기
7. /api/v1/user/password 비밀번호 변경

## 기능구현 상세

### 1. /api/v1/auth/check/phone-number/message 핸드폰 인증메세지 전송
- 기능상세
    - 회원가입 전 핸드폰 인증을 위한 메세지 요청
    - 요청값은 아래와 같습니다.
    ```text
    phoneNumber - "010-1234-5678"
    ```
    - 전화번호 양식은 위의 샘플데이터와 같이 - 를 포함한 값으로 요청을 보냅니다.
    - 인증메세지의 유효시간은 3분 입니다.
    
### 2. /api/v1/auth/check/phone-number 핸드폰 인증
- 기능상세
    - 핸드폰 인증 절차입니다.
    - 핸드폰 인증의 유지기간은 1일(하루) 입니다.
    - (필수) 핸드폰 인증을 받기 전에 메세지를 먼저 받아야합니다.
    - 요청값은 아래와 같습니다.
    ```text
    verificationCode - "1234"
    phoneNumber - "010-1234-5678"
    ```
    
- 예외사항
    - 해당 전화번호에 인증 메세지를 받지 않은 경우
        -(message) 해당하는 핸드폰 번호를 찾을 수 없습니다. 핸드폰 인증 메세지를 먼저 받아주세요.

### 3. /api/v1/user/signup 회원가입
- 기능상세
    - 핸드폰 인증 절차 후 회원가입이 가능합니다.
    - 비밀번호는 __단방향__ 암호화를 한다.
    - 주민등록번호는 __양방향__ 암호화를 한다.
    - 요청값은 아래와 같습니다.
    ```text
    loginId - "test"
    password - "test1234"
    name - "홍길동"
    regNo - "920910-1234567"
    phoneNumber - "010-1234-5678"
    ```
    - (validate) 비밀번호는 영어 + 숫자조합 최소 8자 이상
    - (validate) 주민등록번호 양식 확인
    - (validate) 핸드폰 번호는 - 를 포함한 값으로 요청
  
- 예외처리
    - 핸드폰 인증이 되지 않는 경우
      - (message) 핸드폰 번호 인증을 먼저해주세요.
    - 핸드폰 인증 후 1일의 시간이 지났을 경우
      - (message) 핸드폰 번호 인증을 먼저해주세요.
    - 기존 아이디 존재여부 확인
      - (message) 해당 계정은 이미 존재합니다.
  
### 4. /api/v1/auth/login 로그인
- 기능상세
    - 로그인을 하기 위하여는 회원가입이 필수입니다.
    - 로그인을 할 수 있는 방법은 loginId(아이디), phoneNumber(핸드폰번호), regNo(주민등록번호) 입니다.
    - 위 3가지 중 한 가지의 방법으로 로그인 할 수 있습니다.
    - 요청값은 아래와 같습니다.
    ```text
    loginId - "test" 
    phoneNumber - "010-1234-5678"
    regNo - "920910-1234567"
    password - "test1234"
    ```
    - 3가지 중 하나라도 입력이 되지 않는 다면 예외처리 됩니다.
    - 동시에 여러가지 입력값이 주어졌을 경우 loginId, phoneNumber, regNo 순으로 우선순위가 높고 우선순위가 가장 높은 것을 선택해서 로그인 시도한다.

- 예외처리
    - 아이디, 비밀번호가 일치하지 않을 경우 안내메세지를 보낸다.
    - 핸드폰번호, 비밀번호가 일치하지 않을 경우 안내메세지를 보낸다.
    - 주민등록번호, 비밀번호가 일치하지 않을 경우 안내메세지를 보낸다.
        - (message) 아이디 혹은 비밀번호를 확인해주세요.
    
### 5. /api/v1/auth/token 토큰 갱신
- 기능상세
    - accessToken 이 만료되었을 경우 refreshToken 으로 계속 로그인 상태가 지속가능할 수 있도록한다.
    - 요청 데이터는 아래와 같습니다.
    ```text
    id - 1
    refreshToken - "eyJ0eXBlIjoidG9rZW4iLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjUwNjk3NzAxLCJ1c2VySWQiOiJ0ZXN0In0._C7Hf1dVpBFG3DfVxAdrUjDnN2S145amYOtLgumCJKA"
    ```
    
- 예외사항
    - 요청 refreshToken 과 DB refreshToken 값이 일치하지 않을 경우
        - (message) refresh token 이 비정상적입니다. 재발급받아 주시기 바랍니다.
    - DB 에서 해당하는 refreshToken id 값을 찾을 수 없을 경우
        - (message) refresh token 을 찾을 수 없습니다.
    - refreshToken 이 유효하지 않을 경우
        - (message) refresh token 이 유효하지 않습니다
    
### 6. /api/v1/user/info 내 정보 보기
- 기능상세
    - Jwt token 을 기반으로 유저의 정보를 조회합니다.
    - Jwt token 값은 accessToken 을 활용하고, Header 값에 넣어준다.
    - Bearer + accessToken 형식으로 보내야해 한다.
    - 요청값은 아래와 같습니다.
    ```text
    Authorization: Bearer eyJ0eXBlIjoidG9rZW4iLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjQ5NDA0ODE5LCJ1c2VySWQiOiJ0ZXN0In0.Px8P_yEe3vFKvODrj3kQrFPF6sOJPa3fznUGDWdVW38
    ```
  
- 예외사항
    - token 을 입력하지 않을 경우
        - (message) 인증완료 후 이용가능합니다.
    - accessToken 이 만료되었을 경우
        - (message) 토큰인증이 만료 되었습니다.
  
### 7. /api/v1/user/password 비밀번호 변경
- 기능상세
    - 핸드폰 인증 절차가 필요하다.
    - 비밀번호를 변경한다.
    - 비밀번호 변경 후 보안강화를 위하여 토큰 정보(accessToken, refreshToken) 정보를 초기화한다.
    - 요청값은 아래와 같습니다.
    ```text
    phoneNumber - "010-1234-5678"
    password - "newtest1234"
    ```
- 예외사항
    - 본인인증을 하지 않을 경우
      - (message) 핸드폰 번호 인증을 먼저해주세요.
        
## 검증방법
- 테스트 코드를 통하여 API 검증을 진행한다.
  ![testScreenShot](./img/test_code.png)
- swagger 통해 test 할 수 있습니다.

### swagger test
- swagger 테스트 순서
1. 핸드폰 인증 메세지 발송
2. 핸드폰 인증
3. 회원가입
4. 로그인
5. accessToken 입력
   ![swagger_1](img/swagger_2.png)
   ![swagger_2](img/swagger_1.png)
6. 내정보보기
7. 비밀번호 변경