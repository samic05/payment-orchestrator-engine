package dev.samic.financial.payment.orchestrator.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Smoke test básico — verifica que la clase main existe y es invocable.
 * NO usa @SpringBootTest porque requeriría MongoDB y RabbitMQ corriendo.
 */
class PaymentOrchestratorEngineApplicationTests {

	@Test
	void mainClassExists() {
		assertDoesNotThrow(() -> Class.forName(
				"dev.samic.financial.payment.orchestrator.engine.PaymentOrchestratorEngineApplication"));
	}
}
