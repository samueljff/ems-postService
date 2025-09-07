package com.fonseca.algaposts.postService.common;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI algaPostsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AlgaPosts - Post Service API")
                        .version("1.0.0")
                        .description("""
                                ## API do Serviço de Posts AlgaPosts
                                
                                Este microserviço é responsável por:
                                - **Criação de posts** com validação automática
                                - **Processamento de texto assíncrono** via RabbitMQ
                                - **Cálculo automático** de palavras e valores
                                - **Consulta paginada** de posts com resumos
                                - **Tratamento robusto de erros** e timeouts
                                
                                ### Fluxo de Criação:
                                1. Post é submetido via POST
                                2. Validação de campos obrigatórios
                                3. Persistência inicial no banco H2
                                4. Envio para fila de processamento (RabbitMQ)
                                5. Processamento assíncrono pelo Text Processor Service
                                6. Atualização com dados calculados (wordCount, calculatedValue)
                                
                                ### Arquitetura:
                                - **Spring Boot** + **Spring Data JPA**
                                - **RabbitMQ** para mensageria assíncrona
                                - **H2 Database** para persistência
                                - **Bean Validation** para validações
                                
                                Desenvolvido durante o curso **EMS AlgaWorks**
                                """
                        ));
    }
}