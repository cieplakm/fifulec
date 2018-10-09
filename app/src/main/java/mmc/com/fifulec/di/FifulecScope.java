package mmc.com.fifulec.di;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import dagger.releasablereferences.CanReleaseReferences;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@CanReleaseReferences
@Scope
public @interface FifulecScope {
}
