package com.hopetribe.silentinstallation;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
  * @ClassName: ShellUtils
  * @Description: Shell工具类
  * @author ericczhuang
  * @date 2014-8-8 下午4:18:38
  *
  */
public class ShellUtils {

    public static final String COMMAND_SU       = "su";
    public static final String COMMAND_SH       = "sh";
    public static final String COMMAND_EXIT     = "exit\n";
    public static final String COMMAND_LINE_END = "\n";

    /**
      * @Method: checkRootPermission
      * @Description: check whether has root permission
      * @return	返回类型：boolean 
      */
    public static boolean checkRootPermission() {
        return execCommand("echo root", true, false).result == 0;
    }

   
    /**
      * @Method: execCommand
      * @Description: 执行shell命令，默认返回执行结果
      * @param command
      * @param isRoot，是否需要root权限执行
      * @return	
      * 返回类型：CommandResult 
      */
    public static CommandResult execCommand(String command, boolean isRoot) {
        return execCommand(new String[] {command}, isRoot, true);
    }

    /**
      * @Method: execCommand
      * @Description: 执行shell命令，默认返回执行结果
      * @param command
      * @param isRoot，是否需要root权限执行
      * @return 
      * 返回类型：CommandResult 
      */
    public static CommandResult execCommand(List<String> commands, boolean isRoot) {
        return execCommand(commands == null ? null : commands.toArray(new String[] {}), isRoot, true);
    }


    /**
      * @Method: execCommand
      * @Description: 执行shell命令，默认返回执行结果
      * @param command
      * @param isRoot，是否需要root权限执行
      * @return 
      * 返回类型：CommandResult 
      */
    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        return execCommand(commands, isRoot, true);
    }


    /**
      * @Method: execCommand
      * @Description: 执行shell命令，默认返回执行结果
      * @param command
      * @param isRoot，是否需要root权限执行
      * @return 
      * 返回类型：CommandResult 
      */
    public static CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(new String[] {command}, isRoot, isNeedResultMsg);
    }


    /**
      * @Method: execCommand
      * @Description: 执行shell命令，默认返回执行结果
      * @param command
      * @param isRoot，是否需要root权限执行
      * @return 
      * 返回类型：CommandResult 
      */
    public static CommandResult execCommand(List<String> commands, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(commands == null ? null : commands.toArray(new String[] {}), isRoot, isNeedResultMsg);
    }

    /**
     * execute shell commands
     * 
     * @param commands command array
     * @param isRoot whether need to run with root
     * @param isNeedResultMsg whether need result msg
     * @return <ul>
     *         <li>if isNeedResultMsg is false, {@link CommandResult#successMsg} is null and
     *         {@link CommandResult#errorMsg} is null.</li>
     *         <li>if {@link CommandResult#result} is -1, there maybe some excepiton.</li>
     *         </ul>
     */
    
    /**
      * @Method: execCommand
      * @Description: 执行shell命令
      * @param commands
      * @param isRoot 是否需要root权限执行   
      * @param isNeedResultMsg 是否需要返回执行结果
      * @return	
      * 返回类型：CommandResult 
      */
    public static CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, null, null);
        }

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;

        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }

                // donnot use os.writeBytes(commmand), avoid chinese charset error
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();

            result = process.waitFor();
            // get command result
            if (isNeedResultMsg) {
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.append(s);
                }
                while ((s = errorResult.readLine()) != null) {
                    errorMsg.append(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(result, successMsg == null ? null : successMsg.toString(), errorMsg == null ? null
                : errorMsg.toString());
    }

    /**
      * @ClassName: CommandResult
      * @Description: 命令执行结果
      *         result:命令执行结果，0：正常；其它：错误
      *         successMsg：命令执行成功；
      *         errorMsg：命令执行错误；
      * @author ericczhuang
      * @date 2014-8-8 下午4:23:22
      *
      */
    public static class CommandResult {

        /** result of command **/
        public int    result;
        /** success message of command result **/
        public String successMsg;
        /** error message of command result **/
        public String errorMsg;

        public CommandResult(int result) {
            this.result = result;
        }

        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }
    }
}
