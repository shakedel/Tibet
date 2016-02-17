package tau.cs.wolf.tibet.percentage_apbt.matching;

import tau.cs.wolf.tibet.percentage_apbt.data.CharArr;
import tau.cs.wolf.tibet.percentage_apbt.data.IntArr;
import tau.cs.wolf.tibet.percentage_apbt.data.Slicable;

public class ApbtFactory {
	@SuppressWarnings("unchecked")
	public static <R extends Slicable<R>> Apbt<R> newApbt(Class<R> clazz) {
		@SuppressWarnings("rawtypes")
		Class<? extends Apbt> apbtClazz;
		if (clazz == IntArr.class) {
			apbtClazz = ApbtInt.class;
		} else if (clazz == CharArr.class) {
			apbtClazz = ApbtChar.class;
		} else {
			throw new IllegalArgumentException("Unknown class: "+clazz);
		}
		try {
			return (Apbt<R>) apbtClazz.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
