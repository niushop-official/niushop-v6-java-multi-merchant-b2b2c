package com.niu.core.job.sys;

import com.niu.core.common.component.context.queue.TaskArgument;
import com.niu.core.common.utils.file.FileTools;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 校验消息队列是否正常运行
 */
@Slf4j
@Service
public class CheckJobService {

    @Resource
    private ExecutorService executorService;

    public void doJob(TaskArgument taskArgument) {
        String file = (String) taskArgument.get("file");
        if (file == null || file.isEmpty()) {
            return;
        }
        FileTools.write(file, String.valueOf(System.currentTimeMillis() / 1000));
        executorService.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(8);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            FileTools.deleteFile(file);
        });
    }
}
