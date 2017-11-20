package betrip.kitttn.architecturecomponentstest.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * @author kitttn
 */

@Scope @Retention(RetentionPolicy.RUNTIME)
public @interface LifecycleAware {
}
