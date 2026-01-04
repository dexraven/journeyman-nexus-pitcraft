package com.journeyman.nexus.pitcraft;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.mockito.Mockito.*;

class NexusApplicationTest {

    @Test
    void main_StartsApplication() {
        // We use try-with-resources to mock the static SpringApplication.run method.
        // This prevents the actual server from trying to start (and conflicting on port 8080)
        // while still executing the code inside your main() method.
        try (MockedStatic<SpringApplication> mockedSpring = mockStatic(SpringApplication.class)) {

            // 1. Arrange: Define what happens when run() is called
            mockedSpring.when(() -> SpringApplication.run(NexusApplication.class, new String[]{}))
                    .thenReturn(mock(ConfigurableApplicationContext.class));

            // 2. Act: Call the actual main method
            NexusApplication.main(new String[]{});

            // 3. Assert: Verify SpringApplication.run was called exactly once
            mockedSpring.verify(() -> SpringApplication.run(NexusApplication.class, new String[]{}));
        }
    }
}