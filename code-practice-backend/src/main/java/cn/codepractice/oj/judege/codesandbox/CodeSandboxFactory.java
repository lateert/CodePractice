package cn.codepractice.oj.judege.codesandbox;

import cn.codepractice.oj.judege.codesandbox.impl.ExampleCodeSandbox;
import cn.codepractice.oj.judege.codesandbox.impl.RemoteCodeSandbox;
import cn.codepractice.oj.judege.codesandbox.impl.ThirdPartyCodeSandbox;

public class CodeSandboxFactory {

    public static CodeSandbox newInstance(String type) {
        switch (type) {
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            case "example":
            default:
                return new ExampleCodeSandbox();
        }
    }

}
