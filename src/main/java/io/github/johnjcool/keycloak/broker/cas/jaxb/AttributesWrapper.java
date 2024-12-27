package io.github.johnjcool.keycloak.broker.cas.jaxb;


import io.jbock.util.Either;
import org.jboss.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlType;

import org.w3c.dom.Element;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
@XmlType
public class AttributesWrapper {

	private final List<JAXBElement<String>> attributes = new ArrayList<>();
	private final List<io.github.johnjcool.keycloak.broker.cas.model.Function> mFunctions = new ArrayList<>();

	@XmlAnyElement
	public List<JAXBElement<String>> getAttributes() {
		return attributes;
	}

	@XmlAnyElement
	public List<io.github.johnjcool.keycloak.broker.cas.model.Function> getmFunctions() {
		return mFunctions;
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
		List<?> attrs = new ArrayList<>();
    Logger logger = Logger.getLogger(AttributesWrapper.class);
    logger.infof("------- %s", this.attributes);

		Map<String, Object> allAttributesMap = attrs.stream().collect(Collectors.toMap(AttributesWrapper::extractLocalName, AttributesWrapper::extractTextContent));
		ObjectMapper objectMapper = new ObjectMapper();
		Function<List<io.github.johnjcool.keycloak.broker.cas.model.Function>, String> functionToJsonString =
			(x) -> {
				try {
					return objectMapper.writeValueAsString(mFunctions);
				} catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      };
		String functionsJsonString = functionToJsonString.apply(mFunctions);
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
