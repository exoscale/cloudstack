package io.exo.cloudstack.restrictions;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Class representing the data for a single restriction (laoded from Yaml configuration file in our case).
 */
public class Restriction {
    private String serviceOfferingName = null;
    private Long maxTemplateSize = null;
    private String unauthorizedTemplateName = null;
    private Pattern templateNamePattern = null;
    private List<String> authorizedOrgs = null;

    public String getServiceOfferingName() {
        return serviceOfferingName;
    }

    public void setServiceOfferingName(String serviceOfferingName) {
        this.serviceOfferingName = serviceOfferingName;
    }

    public Long getMaxTemplateSize() {
        return maxTemplateSize;
    }

    public void setMaxTemplateSize(Long maxTemplateSize) {
        this.maxTemplateSize = maxTemplateSize;
    }

    public void setTemplateName(String templateName) {
        this.unauthorizedTemplateName = templateName;
    }

    public String getTemplateName() {
        return unauthorizedTemplateName;
    }

    public String getUnauthorizedTemplateName() {
        return unauthorizedTemplateName;
    }

    public void setUnauthorizedTemplateName(String unauthorizedTemplateName) {
        this.unauthorizedTemplateName = unauthorizedTemplateName;
    }

    public Pattern getTemplateNamePattern() {
        if (unauthorizedTemplateName != null && templateNamePattern == null) {
            templateNamePattern = Pattern.compile(unauthorizedTemplateName);
        }
        return templateNamePattern;
    }

    public void setAuthorizedOrgs(List<String> authorizedOrgs) {
        this.authorizedOrgs = authorizedOrgs;
    }

    public List<String> getAuthorizedOrgs() {
        return authorizedOrgs;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Restriction {");
        if (serviceOfferingName != null) {
            sb.append("serviceOffering: ");
            sb.append(serviceOfferingName);
            sb.append(", ");
        }
        if (unauthorizedTemplateName != null) {
            sb.append("unauthorizedTemplateName: ");
            sb.append(unauthorizedTemplateName);
            sb.append(", ");
        }
        if(maxTemplateSize != null) {
            sb.append("maxTemplateSize: ");
            sb.append(maxTemplateSize);
            sb.append(", ");
        }
        if (authorizedOrgs != null) {
            sb.append("authorizedOrgs: ");
            sb.append(authorizedOrgs.size());
            sb.append(", ");
        }
        sb.replace(sb.length()-2, sb.length(), "");
        sb.append("}");
        return  sb.toString();
    }

}
