package uk.gov.di.ipv.cri.address.library.service;

import software.amazon.lambda.powertools.parameters.ParamManager;
import software.amazon.lambda.powertools.parameters.SSMProvider;
import software.amazon.lambda.powertools.parameters.SecretsProvider;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ConfigurationService {

    private static final long DEFAULT_SESSION_ADDRESS_TTL_IN_SECS = 172800L;
    private static final long DEFAULT_BEARER_TOKEN_TTL_IN_SECS = 3600L;
    private static final long DEFAULT_MAXIMUM_JWT_TTL = 3600L;
    private final SSMProvider ssmProvider;
    private final SecretsProvider secretsProvider;
    private final String parameterPrefix;

    public ConfigurationService(
            SSMProvider ssmProvider, SecretsProvider secretsProvider, String parameterPrefix) {
        this.ssmProvider = ssmProvider;
        this.secretsProvider = secretsProvider;
        this.parameterPrefix = parameterPrefix;
    }

    public ConfigurationService() {
        this.ssmProvider = ParamManager.getSsmProvider();
        this.secretsProvider = ParamManager.getSecretsProvider();
        this.parameterPrefix =
                Objects.requireNonNull(
                        System.getenv("AWS_STACK_NAME"), "env var AWS_STACK_NAME required");
    }

    public String getSessionTableName() {
        return ssmProvider.get(getParameterName(SSMParameterName.SESSION_TABLE_NAME));
    }

    public String getPersonIdentityTableName() {
        return ssmProvider.get(getParameterName(SSMParameterName.PERSON_IDENTITY_TABLE_NAME));
    }

    public String getAddressTableName() {
        return ssmProvider.get(getParameterName(SSMParameterName.ADDRESS_TABLE_NAME));
    }

    public long getSessionTtl() {
        return Optional.ofNullable(ssmProvider.get(getParameterName(SSMParameterName.SESSION_TTL)))
                .map(Long::valueOf)
                .orElse(DEFAULT_SESSION_ADDRESS_TTL_IN_SECS);
    }

    public String getParameterName(SSMParameterName parameterName) {
        return String.format("/%s/%s", parameterPrefix, parameterName.parameterName);
    }

    public Map<String, String> getParametersForPath(String path) {
        String format = String.format("/%s/%s", parameterPrefix, path);
        return ssmProvider.recursive().getMultiple(format.replace("//", "/"));
    }

    public URI getDynamoDbEndpointOverride() {
        String dynamoDbEndpointOverride = System.getenv("DYNAMODB_ENDPOINT_OVERRIDE");
        if (dynamoDbEndpointOverride != null && !dynamoDbEndpointOverride.isEmpty()) {
            return URI.create(System.getenv("DYNAMODB_ENDPOINT_OVERRIDE"));
        }
        return null;
    }

    public String getAddressAuthCodesTableName() {
        return System.getenv("ADDRESS_AUTH_CODES_TABLE_NAME");
    }

    public long getBearerAccessTokenTtl() {
        return Optional.ofNullable(System.getenv("BEARER_TOKEN_TTL"))
                .map(Long::valueOf)
                .orElse(DEFAULT_BEARER_TOKEN_TTL_IN_SECS);
    }

    public String getAccessTokensTableName() {
        return System.getenv("ADDRESS_ACCESS_TOKENS_TABLE_NAME");
    }

    public enum SSMParameterName {
        SESSION_TABLE_NAME("SessionTableName"),
        SESSION_TTL("SessionTtl"),
        ORDNANCE_SURVEY_API_KEY("OrdnanceSurveyAPIKey"),
        ORDNANCE_SURVEY_API_URL("OrdnanceSurveyAPIURL"),
        ADDRESS_CRI_AUDIENCE("AddressCriAudience"),
        MAXIMUM_JWT_TTL("MaxJwtTtl"),
        VERIFIABLE_CREDENTIAL_SIGNING_KEY_ID("verifiableCredentialKmsSigningKeyId"),
        VERIFIABLE_CREDENTIAL_ISSUER("verifiable-credential/issuer"),
        AUTH_REQUEST_KMS_ENCRYPTION_KEY_ID("AuthRequestKmsEncryptionKeyId"),
        ADDRESS_TABLE_NAME("AddressTableName"),
        PERSON_IDENTITY_TABLE_NAME("PersonIdentityTableName");

        public final String parameterName;

        SSMParameterName(String parameterName) {
            this.parameterName = parameterName;
        }
    }

    public String getOsApiKey() {
        return secretsProvider.get(getParameterName(SSMParameterName.ORDNANCE_SURVEY_API_KEY));
    }

    public String getOsPostcodeAPIUrl() {
        return ssmProvider.get(getParameterName(SSMParameterName.ORDNANCE_SURVEY_API_URL));
    }

    public String getAddressCriAudienceIdentifier() {
        return ssmProvider.get(getParameterName(SSMParameterName.ADDRESS_CRI_AUDIENCE));
    }

    public long getMaxJwtTtl() {
        return Optional.ofNullable(
                        ssmProvider.get(getParameterName(SSMParameterName.MAXIMUM_JWT_TTL)))
                .map(Long::valueOf)
                .orElse(DEFAULT_MAXIMUM_JWT_TTL);
    }

    public String getVerifiableCredentialIssuer() {
        return ssmProvider.get(getParameterName(SSMParameterName.VERIFIABLE_CREDENTIAL_ISSUER));
    }

    public String getVerifiableCredentialKmsSigningKeyId() {
        return ssmProvider.get(
                getParameterName(SSMParameterName.VERIFIABLE_CREDENTIAL_SIGNING_KEY_ID));
    }

    public String getSqsAuditEventQueueUrl() {
        return System.getenv("SQS_AUDIT_EVENT_QUEUE_URL");
    }

    public String getKmsEncryptionKeyId() {
        return ssmProvider.get(
                getParameterName(SSMParameterName.AUTH_REQUEST_KMS_ENCRYPTION_KEY_ID));
    }
}
