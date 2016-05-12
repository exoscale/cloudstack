package io.exo.cloudstack.restrictions;

import java.util.regex.Pattern;

/**
 * Class representing the data for a single restriction (laoded from Yaml configuration file in our case).
 */
public class Restriction {
    private Long maxTemplateSize = null;
    private String templateName = null;
    private Pattern templateNamePattern = null;
    private String serviceOfferingName = null;

    public Long getMaxTemplateSize() {
        return maxTemplateSize;
    }

    public void setMaxTemplateSize(Long maxTemplateSize) {
        this.maxTemplateSize = maxTemplateSize;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getServiceOfferingName() {
        return serviceOfferingName;
    }

    public void setServiceOfferingName(String serviceOfferingName) {
        this.serviceOfferingName = serviceOfferingName;
    }

    public Pattern getTemplateNamePattern() {
        if (templateName != null && templateNamePattern == null) {
            templateNamePattern = Pattern.compile(templateName);
        }
        return templateNamePattern;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Restriction {");
        if (serviceOfferingName != null) {
            sb.append("serviceOffering: ");
            sb.append(serviceOfferingName);
            sb.append(", ");
        }
        if (templateName != null) {
            sb.append("templateName: ");
            sb.append(templateName);
            sb.append(", ");
        }
        if(maxTemplateSize != null) {
            sb.append("maxTemplateSize: ");
            sb.append(maxTemplateSize);
            sb.append(", ");
        }
        sb.replace(sb.length()-2, sb.length(), "");
        sb.append("}");
        return  sb.toString();
    }

}
