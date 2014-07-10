/*
 * Copyright (c) 2006-2014 Rogério Liesenfeld
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.mockups;

import java.lang.reflect.Type;
import org.jetbrains.annotations.*;

import mockit.*;
import mockit.external.asm4.*;
import mockit.internal.*;
import mockit.internal.capturing.*;
import mockit.internal.util.*;

public final class CaptureOfMockedUpImplementations extends CaptureOfImplementations
{
   private final MockClassSetup mockClassSetup;

   public CaptureOfMockedUpImplementations(MockUp<?> mockUp, Type baseType)
   {
      Class<?> baseClassType = Utilities.getClassType(baseType);
      mockClassSetup = new MockClassSetup(baseClassType, baseType, mockUp, null);
   }

   @NotNull
   @Override
   protected BaseClassModifier createModifier(
      @Nullable ClassLoader cl, @NotNull ClassReader cr, @NotNull String baseTypeDesc)
   {
      return mockClassSetup.createClassModifier(cr);
   }

   @Override
   protected void redefineClass(@NotNull Class<?> realClass, @NotNull byte[] modifiedClass)
   {
      mockClassSetup.applyClassModifications(realClass, modifiedClass);
   }

   @Nullable
   public <T> Class<T> apply()
   {
      @SuppressWarnings("unchecked") Class<T> baseType = (Class<T>) mockClassSetup.realClass;
      Class<T> baseClassType = baseType;
      Class<T> mockedClass = null;

      if (baseType.isInterface()) {
         mockedClass = new MockedImplementationClass<T>(mockClassSetup.mockUp).createImplementation(baseType);
         baseClassType = mockedClass;
      }

      if (baseClassType != Object.class) {
         redefineClass(baseClassType, mockit.external.asm4.Type.getInternalName(baseType));
         mockClassSetup.validateThatAllMockMethodsWereApplied();
      }

      makeSureAllSubtypesAreModified(baseType, false);
      return mockedClass;
   }
}
