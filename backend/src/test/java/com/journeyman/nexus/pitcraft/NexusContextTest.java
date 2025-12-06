package com.journeyman.nexus.pitcraft;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class NexusContextTest {

    @MockBean
    private ChatModel chatModel;

    @Test
    void contextLoads() {}
}