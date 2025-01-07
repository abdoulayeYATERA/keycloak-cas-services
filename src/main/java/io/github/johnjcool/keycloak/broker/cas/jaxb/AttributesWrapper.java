package io.github.johnjcool.keycloak.broker.cas.jaxb;


import io.github.johnjcool.keycloak.broker.cas.model.ENTFunction;
import org.jboss.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;

import org.w3c.dom.Element;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
@XmlAccessorType(XmlAccessType.FIELD)
public class AttributesWrapper {

	@XmlAnyElement
	private final List<JAXBElement<String>> attributes = new ArrayList<>();

	@XmlElement(name="ENTPersonFunctions", namespace = "http://www.yale.edu/tp/cas")
	private final List<ENTFunction> mENTFunctions = new ArrayList<>();

	public List<JAXBElement<String>> getAttributes() {
		return attributes;
	}

	public List<ENTFunction> getmENTFunctions() {
		return mENTFunctions;
	}

	/**
	 * <p>
	 * To Read-Only Map
	 * </p>
	 * 
	 * @return
	 */
	public Map<String, Object> toMap() {
		// Note: Due to type erasure, you cannot use properties.stream() directly when unmashalling is used..
		List<?> attrs = attributes;
    Logger logger = Logger.getLogger(AttributesWrapper.class);

		Map<String, Object> allAttributesMap = attrs.stream().collect(Collectors.toMap(AttributesWrapper::extractLocalName, AttributesWrapper::extractTextContent));
		ObjectMapper objectMapper = new ObjectMapper();
		Function<List<ENTFunction>, String> functionToJsonString =
			(x) -> {
				try {
					return objectMapper.writeValueAsString(mENTFunctions);
				} catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      };
		String functionsJsonString = functionToJsonString.apply(mENTFunctions);
		allAttributesMap.put("functions", functionsJsonString);
		return allAttributesMap;


	}

	/**
	 * <p>
	 * Extract local name from <code>obj</code>, whether it's javax.xml.bind.JAXBElement or org.w3c.dom.Element;
	 * </p>
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static String extractLocalName(final Object obj) {
		Map<Class<?>, Function<? super Object, String>> strFuncs = new HashMap<>();
		strFuncs.put(JAXBElement.class, (jaxb) -> ((JAXBElement<String>) jaxb).getName().getLocalPart());
		strFuncs.put(Element.class, ele -> ((Element) ele).getLocalName());
		return extractPart(obj, strFuncs).orElse("");
	}

	/**
	 * <p>
	 * Extract text content from <code>obj</code>, whether it's javax.xml.bind.JAXBElement or org.w3c.dom.Element;
	 * </p>
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static String extractTextContent(final Object obj) {
		Map<Class<?>, Function<? super Object, String>> strFuncs = new HashMap<>();
		strFuncs.put(JAXBElement.class, (jaxb) -> ((JAXBElement<String>) jaxb).getValue());
		strFuncs.put(Element.class, ele -> ((Element) ele).getTextContent());
		return extractPart(obj, strFuncs).orElse("");
	}

	/**
	 * Check class type of <code>obj</code> according to types listed in <code>strFuncs</code> keys, then extract some string part from it according
	 * to the extract function specified in <code>strFuncs</code> values.
	 * 
	 * @param obj
	 * @param strFuncs
	 * @return
	 */
	private static <ObjType, T> Optional<T> extractPart(final ObjType obj, final Map<Class<?>, Function<? super ObjType, T>> strFuncs) {
		for (Class<?> clazz : strFuncs.keySet()) {
			if (clazz.isInstance(obj)) {
				return Optional.of(strFuncs.get(clazz).apply(obj));
			}
		}
		return Optional.empty();
	}
}
