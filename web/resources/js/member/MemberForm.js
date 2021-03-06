function memberFormChangeDetector() {
    let formTag = document.querySelector('#MemberRegisterForm');
    let userIdInput = null;
    let passwordInput = null;
    let userNameInput = null;
    let submitBtn = null;
    if (formTag != null) {
        userIdInput = formTag.querySelector("input[name='userId']");
        passwordInput = formTag.querySelector("input[name='password']");
        userNameInput = formTag.querySelector("input[name='userName']");
        submitBtn = formTag.querySelector("#MemberCreateSubmitBtn");
    }

    function UserIdNotDuplicates(bool) {
        if (bool !== 'true') {
            putMessageForInputTag("success", userIdInput, "사용 가능한 ID입니다.");
        } else {
            putMessageForInputTag("danger", userIdInput, "중복된 ID입니다.");
        }
    }

    function FormElementsValidator() {
        try {
            formTag.querySelectorAll("input").forEach(value => {
                    if (value.getAttribute("value-status") === 'error') {
                        throw "Invalid";
                    }
                }
            );
            return true;
        } catch (e) {
            return false;
        }
    }

    let httpRequest = new XMLHttpRequest();
    httpRequest.onreadystatechange = () => {
        if (httpRequest.readyState === XMLHttpRequest.DONE) {
            if (httpRequest.status === 200) {
                UserIdNotDuplicates(httpRequest.response);
            } else {
                // console.log("HTTP Request(status) ERROR: " + httpRequest.status);
            }
        } else {
            // console.log("HTTP Request(readyState) ERROR: " + httpRequest.readyState);
        }
    };

    if (userIdInput != null) {
        userIdInput.addEventListener('input', (evt) => {
            const inputValue = evt.currentTarget.value;
            if (formatValidator("ID", inputValue)) {
                putMessageForInputTag("danger", userIdInput, "아이디는 영문자, 숫자, \'_\' 만 입력 가능합니다.")
            } else if (inputValue.length < 2) {
                putMessageForInputTag("danger", userIdInput, "아이디는 최소 2자 이상이어야 합니다.");
            } else {
                putMessageForInputTag("success", userIdInput, "");
                // httpRequest.open('GET', '/check/userid/duplicates?userId=' + encodeURIComponent(inputValue));
                // httpRequest.send(null);
                httpRequest.open('POST', '/member/userId-duplicatesCheck');
                httpRequest.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
                httpRequest.send("userId=" + encodeURIComponent(inputValue));
            }
        });
    }

    if (passwordInput != null) {
        passwordInput.addEventListener('input', (evt) => {
            let inputValue = evt.currentTarget.value;
            if (evt.currentTarget.value.length < 4) {
                passwordInput.nextElementSibling.setAttribute("type", "hidden");
                passwordInput.nextElementSibling.value = "";
                putMessageForInputTag("danger", passwordInput.nextElementSibling, "패스워드는 최소 4자 이상이어야 합니다.");
                // formTag.querySelector("#member-form-userId-status").setAttribute("class", "color-error");
                // userIdInput.setAttribute("value-status", "ERROR");
                // formTag.querySelector("#member-form-userId-status").innerHTML = "패스워드는 최소 10자 이상이어야 합니다.";
            } else {
                if (/\s/g.test(inputValue)) {
                    putMessageForInputTag("danger", passwordInput.nextElementSibling, "패스워드는 공백이 포함될 수 없습니다.");
                } else {
                    passwordInput.nextElementSibling.setAttribute("type", "password")
                    passwordInput.nextElementSibling.value = "";
                    putMessageForInputTag("info", passwordInput.nextElementSibling, "패스워드를 다시 입력해주세요.");
                    passwordInput.nextElementSibling.addEventListener('input', (evt2) => {
                        if (inputValue === evt2.currentTarget.value) {
                            passwordInput.setAttribute("value-status", "success");
                            putMessageForInputTag("success", passwordInput.nextElementSibling, "패스워드가 일치합니다.");
                        } else
                            putMessageForInputTag("danger", passwordInput.nextElementSibling, "패스워드가 일치하지 않습니다.");
                    });
                }
            }
        });
    }
    if (userNameInput != null) {
        userNameInput.addEventListener('input', (evt) => {
            let inputValue = evt.currentTarget.value;
            if (formatValidator("NAME", inputValue)) {
                putMessageForInputTag("danger", userNameInput, "이름은 한글, 영문, 숫자, \'_\' 만을 포함할 수 있습니다.");
            } else if (evt.currentTarget.value.length < 2) {
                userNameInput.nextElementSibling.value = "";
                putMessageForInputTag("danger", userNameInput, "이름은 최소 2자 이상이어야 합니다.");
                // formTag.querySelector("#member-form-userId-status").setAttribute("class", "color-error");
                // userIdInput.setAttribute("value-status", "ERROR");
                // formTag.querySelector("#member-form-userId-status").innerHTML = "패스워드는 최소 10자 이상이어야 합니다.";
            } else {
                putMessageForInputTag("success", userNameInput, "올바른 형식입니다.");
            }
        });
    }

    if (submitBtn != null) {
        submitBtn.addEventListener('click', () => {
            if (FormElementsValidator()) {
                formTag.submit();
            } else {
                alert("회원 정보를 정확히 입력해주세요.");
            }
        });
    }
}
