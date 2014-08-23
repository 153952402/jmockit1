/*
 * Copyright (c) 2006-2014 Rogério Liesenfeld
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.expectations.injection;

import java.lang.annotation.*;
import java.lang.reflect.*;
import javax.annotation.*;
import javax.inject.*;

import static mockit.internal.util.ClassLoad.*;

import org.jetbrains.annotations.*;

final class InjectionPoint
{
   @Nullable static final Class<? extends Annotation> INJECT_CLASS;
   @Nullable static final Class<? extends Annotation> PERSISTENCE_UNIT_CLASS;
   @Nullable static final Class<? extends Annotation> PERSISTENCE_CONTEXT_CLASS;
   @Nullable static final Class<?> ENTITY_MANAGER_FACTORY_CLASS;
   @Nullable static final Class<?> ENTITY_MANAGER_CLASS;
   static final boolean WITH_INJECTION_API_IN_CLASSPATH;

   static
   {
      INJECT_CLASS = searchTypeInClasspath("javax.inject.Inject");
      PERSISTENCE_UNIT_CLASS = searchTypeInClasspath("javax.persistence.PersistenceUnit");
      PERSISTENCE_CONTEXT_CLASS = searchTypeInClasspath("javax.persistence.PersistenceContext");
      ENTITY_MANAGER_FACTORY_CLASS = searchTypeInClasspath("javax.persistence.EntityManagerFactory");
      ENTITY_MANAGER_CLASS = searchTypeInClasspath("javax.persistence.EntityManager");
      WITH_INJECTION_API_IN_CLASSPATH = INJECT_CLASS != null || PERSISTENCE_UNIT_CLASS != null;
   }

   private InjectionPoint() {}

   static Object wrapInProviderIfNeeded(@NotNull Type type, @NotNull final Object value)
   {
      if (
         INJECT_CLASS != null && type instanceof ParameterizedType && !(value instanceof Provider) &&
         ((ParameterizedType) type).getRawType() == Provider.class
      ) {
         return new Provider<Object>() { @Override public Object get() { return value; } };
      }

      return value;
   }

   static boolean isAnnotated(@NotNull Field field)
   {
      return
         field.isAnnotationPresent(Resource.class) ||
         INJECT_CLASS != null && field.isAnnotationPresent(INJECT_CLASS) ||
         PERSISTENCE_UNIT_CLASS != null && (
            field.isAnnotationPresent(PERSISTENCE_CONTEXT_CLASS) || field.isAnnotationPresent(PERSISTENCE_UNIT_CLASS)
         );
   }

   @NotNull
   static Type getTypeOfInjectionPointFromVarargsParameter(@NotNull Type[] parameterTypes, int varargsParameterIndex)
   {
      Type parameterType = parameterTypes[varargsParameterIndex];

      if (parameterType instanceof Class<?>) {
         return ((Class<?>) parameterType).getComponentType();
      }
      else {
         return ((GenericArrayType) parameterType).getGenericComponentType();
      }
   }
}
