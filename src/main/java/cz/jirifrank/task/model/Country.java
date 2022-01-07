
package cz.jirifrank.task.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Country {
	@JsonProperty("cca3")
	private String cca3;
	@JsonProperty("borders")
	private List<String> borders;
}
