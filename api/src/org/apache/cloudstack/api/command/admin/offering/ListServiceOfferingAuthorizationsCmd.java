package org.apache.cloudstack.api.command.admin.offering;

import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.BaseListCmd;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.response.AccountResponse;
import org.apache.cloudstack.api.response.DomainResponse;
import org.apache.cloudstack.api.response.ListResponse;
import org.apache.cloudstack.api.response.ServiceOfferingAuthorizationResponse;
import org.apache.cloudstack.api.response.ServiceOfferingResponse;

@APICommand(name = "listServiceOfferingAuthorizations", description = "Lists service offering's authorizations.", responseObject = ServiceOfferingAuthorizationResponse.class,
requestHasSensitiveInfo = false, responseHasSensitiveInfo = false)
public class ListServiceOfferingAuthorizationsCmd extends BaseListCmd {
    private static final String s_name = "listserviceofferingauthorizationsresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name = ApiConstants.ID, type = CommandType.UUID, entityType = ServiceOfferingAuthorizationResponse.class, description = "filter the result by the id of a service offering authorization")
    private Long id;

    @Parameter(name = ApiConstants.SERVICE_OFFERING_ID, type = CommandType.UUID, entityType = ServiceOfferingResponse.class, description = "filter the result by the id of a service offering")
    private Long serviceOfferingId;

    @Parameter(name = ApiConstants.DOMAIN_ID, type = CommandType.UUID, entityType = DomainResponse.class, description = "filter the result by the id of a domain")
    private Long domainId;

    @Parameter(name = ApiConstants.ACCOUNT_ID, type = CommandType.UUID, entityType = AccountResponse.class, description = "filter the result by the id of an account")
    private Long accountId;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public Long getId() {
        return id;
    }

    public Long getServiceOfferingId() {
        return serviceOfferingId;
    }

    public Long getDomainId() {
        return domainId;
    }

    public Long getAccountId() {
        return accountId;
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    @Override
    public String getCommandName() {
        return s_name;
    }

    @Override
    public void execute() {
        ListResponse<ServiceOfferingAuthorizationResponse> response = _queryService.searchForServiceOfferingAuthorizations(this);
        response.setResponseName(getCommandName());
        this.setResponseObject(response);
    }
}
