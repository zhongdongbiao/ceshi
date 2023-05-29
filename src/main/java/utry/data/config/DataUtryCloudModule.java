
package utry.data.config;

import utry.core.cloud.module.AbstractSimpleUtryCloudModule;

import javax.annotation.Nonnull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * @author utry
 */

public class DataUtryCloudModule extends AbstractSimpleUtryCloudModule{
    public static final String NAME = "data";
    public static final String MAPPER_BASE_PACKAGE = "utry.data.modular.*.dao";
    public static final String REST_API_PACKAGE = "utry.data";
    public static final String CONTROLLER_PACKAGE = "utry.data";
    public static final String CODE = "100060";
	public static final String OWN_PACKAGE = "utry.data";


	@Override
    public String name() {
        return NAME;
    }

	@Nonnull
	@Override
	public List<String> ownPackages() {
		return Collections.singletonList(OWN_PACKAGE);

	}


	@Override
	public List<String> mapperBasePackages() {
		 return Arrays.asList(
				 MAPPER_BASE_PACKAGE);

	}

	@Nonnull
	@Override
	public List<String> swagger2BasePackages() {
		 return Arrays.asList(
				REST_API_PACKAGE,CONTROLLER_PACKAGE);
	}


	@Override
	public List<String> requiredResources() {
		return Arrays.asList(
                "public"
        );

	}


	@Override
	public String code() {
		return CODE;
	}


	@Override
	public String localServicePath() {
		return "/data/**";
	}
}
