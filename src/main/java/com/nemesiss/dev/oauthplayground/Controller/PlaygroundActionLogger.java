package com.nemesiss.dev.oauthplayground.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

@Component
@Slf4j
public class PlaygroundActionLogger {

    public class CommonLogs {
        public static final String Playground_Created = "Playground Created!";
        public static final String Raise_Authentication_Request = "Authentication request raised!";
        public static final String Do_Authentication_On_AuthCenter = "Processing authentication on OAuth Playground auth center.";
        public static final String Authentication_On_AuthCenter_Failed = "Authentication failed. Credentials mismatch.";
        public static final String Authentication_On_AuthCenter_Successful = "Authentication successful!";
        public static final String Scope_Approved = "Approve scopes:";
        public static final String Exchange_Token_On_DevServer = "Development host is exchanging token";
        public static final String AccessingSecrets = "Accessing secrets:";
    }

    @Autowired
    StringRedisTemplate redisTemplate;

    private static final String DELIMITER = " -- ";

    private static final String PLAYGROUND_LOGGER_PREFIX = "PGLOG-";

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("[yyyy-MM-dd H:mm:ss]");

    public void LogFormatter(String PlaygroundID, String AccessEndpoint, String... contents) {
        String timeStamp = dateFormatter.format(new Date());
        StringBuilder builder = new StringBuilder();
        builder.append(PlaygroundID).append(DELIMITER);
        builder.append(timeStamp);
        builder.append(DELIMITER).append(AccessEndpoint).append(DELIMITER);
        for (String content : contents) {
            builder.append(content).append(" ");
        }
        builder.append("\n");
        WritePlaygroundLog(PlaygroundID, builder.toString());
    }

    public void LogFormatter(long PlaygroundID, String AccessEndpoint, String... contents) {
        LogFormatter(String.valueOf(PlaygroundID), AccessEndpoint, contents);
    }

    public String GetLogForPlayground(String PlaygroundID) {
        String result = redisTemplate.opsForValue().get(PLAYGROUND_LOGGER_PREFIX + PlaygroundID);
        return result == null ? "" : result;
    }

    //    private static final String AppendLogWithExpiredScript = "if redis.call('exists',KEYS[1]) == 0 then redis.call('setex',KEYS[1], 3600,ARGV[1]) return 0 else redis.call('append',KEYS[1],ARGV[1]) return 1 end";
    private static final String AppendLogWithExpiredScript = "if redis.call('exists',KEYS[1]) == 0 then redis.call('setex',KEYS[1], redis.call('ttl',KEYS[2]) ,ARGV[1]) return 0 else redis.call('append',KEYS[1],ARGV[1]) return 1 end";

    private void WritePlaygroundLog(String PlaygroundID, String Log) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(AppendLogWithExpiredScript, Long.class);
        Long Reply = redisTemplate.execute(redisScript, Arrays.asList(PLAYGROUND_LOGGER_PREFIX + PlaygroundID, PlaygroundID), Log);
        log.info("Redis script reply:" + Reply);
    }
}
