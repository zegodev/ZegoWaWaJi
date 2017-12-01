package com.zego.base.utils;

/**
 * Copyright © 2017 Zego. All rights reserved.<br><br>
 *
 * @author realuei on 2017/7/6.
 */

public enum ByteSizeUnit {
    BYTES {
        @Override
        public long toBytes(long size, RADIX_TYPE radix) {
            return size;
        }

        @Override
        public double toKB(long size, RADIX_TYPE radix) {
            return (double)size / (KB(radix) / C);
        }

        @Override
        public double toMB(long size, RADIX_TYPE radix) {
            return (double)size / (MB(radix) / C);
        }

        @Override
        public double toGB(long size, RADIX_TYPE radix) {
            return (double)size / (GB(radix) / C);
        }

        @Override
        public double toTB(long size, RADIX_TYPE radix) {
            return (double)size / (TB(radix) / C);
        }

        @Override
        public double toPB(long size, RADIX_TYPE radix) {
            return (double)size / (PB(radix) / C);
        }
    },
    KB {
        @Override
        public long toBytes(long size, RADIX_TYPE radix) {
            return x(size, KB(radix) / C, MAX / (KB(radix) / C));
        }

        @Override
        public double toKB(long size, RADIX_TYPE radix) {
            return size;
        }

        @Override
        public double toMB(long size, RADIX_TYPE radix) {
            return (double)size / (MB(radix) / KB(radix));
        }

        @Override
        public double toGB(long size, RADIX_TYPE radix) {
            return (double)size / (GB(radix) / KB(radix));
        }

        @Override
        public double toTB(long size, RADIX_TYPE radix) {
            return (double)size / (TB(radix) / KB(radix));
        }

        @Override
        public double toPB(long size, RADIX_TYPE radix) {
            return (double)size / (PB(radix) / KB(radix));
        }
    },
    MB {
        @Override
        public long toBytes(long size, RADIX_TYPE radix) {
            return x(size, MB(radix) / C, MAX / (MB(radix) / C));
        }

        @Override
        public double toKB(long size, RADIX_TYPE radix) {
            return x(size, MB(radix) / KB(radix), MAX / (MB(radix) / KB(radix)));
        }

        @Override
        public double toMB(long size, RADIX_TYPE radix) {
            return size;
        }

        @Override
        public double toGB(long size, RADIX_TYPE radix) {
            return (double)size / (GB(radix) / MB(radix));
        }

        @Override
        public double toTB(long size, RADIX_TYPE radix) {
            return (double)size / (TB(radix) / MB(radix));
        }

        @Override
        public double toPB(long size, RADIX_TYPE radix) {
            return (double)size / (PB(radix) / MB(radix));
        }
    },
    GB {
        @Override
        public long toBytes(long size, RADIX_TYPE radix) {
            return x(size, GB(radix) / C, MAX / (GB(radix) / C));
        }

        @Override
        public double toKB(long size, RADIX_TYPE radix) {
            return x(size, GB(radix) / KB(radix), MAX / (GB(radix) / KB(radix)));
        }

        @Override
        public double toMB(long size, RADIX_TYPE radix) {
            return x(size, GB(radix) / MB(radix), MAX / (GB(radix) / MB(radix)));
        }

        @Override
        public double toGB(long size, RADIX_TYPE radix) {
            return size;
        }

        @Override
        public double toTB(long size, RADIX_TYPE radix) {
            return (double)size / (TB(radix) / GB(radix));
        }

        @Override
        public double toPB(long size, RADIX_TYPE radix) {
            return (double)size / (PB(radix) / GB(radix));
        }
    },
    TB {
        @Override
        public long toBytes(long size, RADIX_TYPE radix) {
            return x(size, TB(radix) / C, MAX / (TB(radix) / C));
        }

        @Override
        public double toKB(long size, RADIX_TYPE radix) {
            return x(size, TB(radix) / KB(radix), MAX / (TB(radix) / KB(radix)));
        }

        @Override
        public double toMB(long size, RADIX_TYPE radix) {
            return x(size, TB(radix) / MB(radix), MAX / (TB(radix) / MB(radix)));
        }

        @Override
        public double toGB(long size, RADIX_TYPE radix) {
            return x(size, TB(radix) / GB(radix), MAX / (TB(radix) / GB(radix)));
        }

        @Override
        public double toTB(long size, RADIX_TYPE radix) {
            return size;
        }

        @Override
        public double toPB(long size, RADIX_TYPE radix) {
            return (double)size / (PB(radix) / TB(radix));
        }
    },
    PB {
        @Override
        public long toBytes(long size, RADIX_TYPE radix) {
            return x(size, PB(radix) / C, MAX / (PB(radix) / C));
        }

        @Override
        public double toKB(long size, RADIX_TYPE radix) {
            return x(size, PB(radix) / KB(radix), MAX / (PB(radix) / KB(radix)));
        }

        @Override
        public double toMB(long size, RADIX_TYPE radix) {
            return x(size, PB(radix) / MB(radix), MAX / (PB(radix) / MB(radix)));
        }

        @Override
        public double toGB(long size, RADIX_TYPE radix) {
            return x(size, PB(radix) / GB(radix), MAX / (PB(radix) / GB(radix)));
        }

        @Override
        public double toTB(long size, RADIX_TYPE radix) {
            return x(size, PB(radix) / TB(radix), MAX / (PB(radix) / TB(radix)));
        }

        @Override
        public double toPB(long size, RADIX_TYPE radix) {
            return size;
        }
    };

    static final long C = 1L;
    static final long MAX = Long.MAX_VALUE;

    static final long KB(RADIX_TYPE type) {
        return C * type.radix;
    }

    static final long MB(RADIX_TYPE type) {
        return KB(type) * type.radix;
    }

    static final long GB(RADIX_TYPE type) {
        return MB(type) * type.radix;
    }

    static final long TB(RADIX_TYPE type) {
        return GB(type) * type.radix;
    }

    static final long PB(RADIX_TYPE type) {
        return TB(type) * type.radix;
    }

    /**
     * Scale d by m, checking for overflow.
     * This has a short name to make above code more readable.
     */
    static private long x(long d, long m, long over) {
        if (d > over) return Long.MAX_VALUE;
        if (d < -over) return Long.MIN_VALUE;
        return d * m;
    }

    static public String toHumanString(long size, RADIX_TYPE radix, int digits) {
        double value;
        String unit;
        if (size >= PB(radix)) {
            value = (double)BYTES.toPB(size, radix);
            unit = "PB";
        } else if (size >= TB(radix)) {
            value = (double)BYTES.toTB(size, radix);
            unit = "TB";
        } else if (size >= GB(radix)) {
            value = (double)BYTES.toGB(size, radix);
            unit = "GB";
        } else if (size >= MB(radix)) {
            value = (double)BYTES.toMB(size, radix);
            unit = "MB";
        } else if (size >= KB(radix)) {
            value = (double)BYTES.toKB(size, radix);
            unit = "KB";
        } else {
            value = (double)size;
            unit = "B";
        }

        String formatTemplate = String.format("%%.%df%%s", digits);
        return String.format(formatTemplate, value, unit);
    }

    public abstract long toBytes(long size, RADIX_TYPE radix);

    public abstract double toKB(long size, RADIX_TYPE radix);

    public abstract double toMB(long size, RADIX_TYPE radix);

    public abstract double toGB(long size, RADIX_TYPE radix);

    public abstract double toTB(long size, RADIX_TYPE radix);

    public abstract double toPB(long size, RADIX_TYPE radix);

    public enum RADIX_TYPE {
        N(1024),
        K(1000);

        int radix;
        RADIX_TYPE(int radix) {
            this.radix = radix;
        }
    }
}
