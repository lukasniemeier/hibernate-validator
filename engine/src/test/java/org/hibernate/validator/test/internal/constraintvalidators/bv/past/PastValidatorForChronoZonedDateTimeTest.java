/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.test.internal.constraintvalidators.bv.past;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.hibernate.validator.internal.constraintvalidators.bv.past.PastValidatorForChronoZonedDateTime;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests for {@link org.hibernate.validator.internal.constraintvalidators.bv.past.PastValidatorForChronoZonedDateTime}.
 *
 * @author Khalid Alqinyah
 */
public class PastValidatorForChronoZonedDateTimeTest {

	private PastValidatorForChronoZonedDateTime constraint;

	@BeforeClass
	public void init() {
		constraint = new PastValidatorForChronoZonedDateTime();
	}

	@Test
	public void testIsValid() {
		assertTrue( constraint.isValid( null, null ), "null fails validation." );

		// Test allowed zone offsets (UTC-18 to UTC+18) with 1 hour increments
		for ( int i = -18; i <= 18; i++ ) {
			ZoneId zone = ZoneId.ofOffset( "UTC", ZoneOffset.ofHours( i ) );
			ChronoZonedDateTime future = ZonedDateTime.now( zone ).plusHours( 1 );
			ChronoZonedDateTime past = ZonedDateTime.now( zone ).minusHours( 1 );
			assertTrue( constraint.isValid( past, null ), "Past ZonedDateTime '" + past + "' fails validation.");
			assertFalse( constraint.isValid( future, null ), "Future ZonedDateTime '" + future + "' validated as past.");
		}
	}
}
