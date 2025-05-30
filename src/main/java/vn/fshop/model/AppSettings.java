package vn.fshop.model;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class AppSettings {
    @Value(value = "fshop@gmail.com")
    private String email;
}

