package uk.gov.di.ipv.cri.common.library.service;

import software.amazon.lambda.powertools.parameters.ParamManager;
import software.amazon.lambda.powertools.parameters.SSMProvider;
import software.amazon.lambda.powertools.parameters.SecretsProvider;
import uk.gov.di.ipv.cri.common.library.annotations.ExcludeFromGeneratedCoverageReport;

import java.net.URI;
import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ConfigurationService {

    private static final String PARAMETER_NAME_FORMAT = "/%s/%s";
    private static final long DEFAULT_BEARER_TOKEN_TTL_IN_SECS = 3600L;
    private final SSMProvider ssmProvider;
    private final SecretsProvider secretsProvider;
    private final String parameterPrefix;
    private final Clock clock;

    @ExcludeFromGeneratedCoverageReport
    public ConfigurationService() {
        this.clock = Clock.systemUTC();
        this.ssmProvider = ParamManager.getSsmProvider();
        this.secretsProvider = ParamManager.getSecretsProvider();
        this.parameterPrefix =
                Objects.requireNonNull(
                        System.getenv("AWS_STACK_NAME"), "env var AWS_STACK_NAME required");
    }

    public ConfigurationService(
            SSMProvider ssmProvider,
            SecretsProvider secretsProvider,
            String parameterPrefix,
            Clock clock) {
        this.ssmProvider = ssmProvider;
        this.secretsProvider = secretsProvider;
        this.parameterPrefix = parameterPrefix;
        this.clock = clock;
    }

    public String getAddressTableName() {
        return ssmProvider.get(getParameterName(SSMParameterName.ADDRESS_TABLE_NAME));
    }

    public String getParameterValue(String parameterName) {
        return ssmProvider.get(
                String.format(PARAMETER_NAME_FORMAT, parameterPrefix, parameterName));
    }

    public String getSecretValue(String secretName) {
        return secretsProvider.get(
                String.format(PARAMETER_NAME_FORMAT, parameterPrefix, secretName));
    }

    public long getSessionTtl() {
        return Long.valueOf(ssmProvider.get(getParameterName(SSMParameterName.SESSION_TTL)));
    }

    public long getSessionExpirationEpoch() {
        return clock.instant().plus(getSessionTtl(), ChronoUnit.SECONDS).getEpochSecond();
    }

    private String getParameterName(SSMParameterName parameterName) {
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
        SESSION_TTL("SessionTtl"),
        ORDNANCE_SURVEY_API_KEY("OrdnanceSurveyAPIKey"),
        ORDNANCE_SURVEY_API_URL("OrdnanceSurveyAPIURL"),
        ADDRESS_CRI_AUDIENCE("AddressCriAudience"),
        MAXIMUM_JWT_TTL("MaxJwtTtl"),
        VERIFIABLE_CREDENTIAL_SIGNING_KEY_ID("verifiableCredentialKmsSigningKeyId"),
        VERIFIABLE_CREDENTIAL_ISSUER("verifiable-credential/issuer"),
        AUTH_REQUEST_KMS_ENCRYPTION_KEY_ID("AuthRequestKmsEncryptionKeyId"),
        ADDRESS_TABLE_NAME("AddressTableName");

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
        return Long.valueOf(ssmProvider.get(getParameterName(SSMParameterName.MAXIMUM_JWT_TTL)));
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

    public String getSqsAuditEventPrefix() {
        return System.getenv("SQS_AUDIT_EVENT_PREFIX");
    }

    public String getKmsEncryptionKeyId() {
        return ssmProvider.get(
                getParameterName(SSMParameterName.AUTH_REQUEST_KMS_ENCRYPTION_KEY_ID));
    }
}
