package net.ncguy.tga;

public enum Format {
        Alpha8(8),
        @Deprecated
        Reserved1(0),
        Luminance8(8),
        @Deprecated
        Reserved2(0),
        Luminance16F(16,true),
        Luminance32F(32,true),
        Luminance8Alpha8(16),
        @Deprecated
        Reserved3(0),
        Luminance16FAlpha16F(32,true),
        @Deprecated
        Reserved4(0),
        @Deprecated
        Reserved5(0),
        BGR8(24), // BGR and ABGR formats are often used on windows systems
        RGB8(24),
        @Deprecated
        Reserved6(0),
        @Deprecated
        Reserved7(0),
        RGB565(16),
        @Deprecated
        Reserved8(0),
        RGB5A1(16),
        RGBA8(32),
        ABGR8(32),
        ARGB8(32),
        BGRA8(32),
        @Deprecated
        Reserved9(0),
        DXT1(4,false,true, false),
        DXT1A(4,false,true, false),
        DXT3(8,false,true, false),
        DXT5(8,false,true, false),
        @Deprecated
        Reserved10(0),
        Depth(0,true,false,false),
        Depth16(16,true,false,false),
        Depth24(24,true,false,false),
        Depth32(32,true,false,false),
        Depth32F(32,true,false,true),
        RGB16F_to_RGB111110F(48,true),
        RGB111110F(32,true),
        RGB16F_to_RGB9E5(48,true),
        RGB9E5(32,true),
        RGB16F(48,true),
        RGBA16F(64,true),
        RGB32F(96,true),
        RGBA32F(128,true),
        @Deprecated
        Reserved11(0),
        Depth24Stencil8(32, true, false, false),
        @Deprecated
        Reserved12(0),
        ETC1(4, false, true, false),
        R8I(8),
        R8UI(8),
        R16I(16),
        R16UI(16),
        R32I(32),
        R32UI(32),
        RG8I(16),
        RG8UI(16),
        RG16I(32),
        RG16UI(32),
        RG32I(64),
        RG32UI(64),
        RGB8I(24),
        RGB8UI(24),
        RGB16I(48),
        RGB16UI(48),
        RGB32I(96),
        RGB32UI(96),
        RGBA8I(32),
        RGBA8UI(32),
        RGBA16I(64),
        RGBA16UI(64),
        RGBA32I(128),
        RGBA32UI(128),
        R16F(16,true),
        R32F(32,true),
        RG16F(32,true),
        RG32F(64,true),
        ;

        private int bpp;
        private boolean isDepth;
        private boolean isCompressed;
        private boolean isFloatingPoint;

        private Format(int bpp){
            this.bpp = bpp;
        }

        private Format(int bpp, boolean isFP){
            this(bpp);
            this.isFloatingPoint = isFP;
        }

        private Format(int bpp, boolean isDepth, boolean isCompressed, boolean isFP){
            this(bpp, isFP);
            this.isDepth = isDepth;
            this.isCompressed = isCompressed;
        }

        /**
         * @return bits per pixel.
         */
        public int getBitsPerPixel(){
            return bpp;
        }

        /**
         * @return True if this format is a depth format, false otherwise.
         */
        public boolean isDepthFormat(){
            return isDepth;
        }

        /**
         * @return True if this format is a depth + stencil (packed) format, false otherwise.
         */
        boolean isDepthStencilFormat() {
            return this == Depth24Stencil8;
        }

        /**
         * @return True if this is a compressed image format, false if
         * uncompressed.
         */
        public boolean isCompressed() {
            return isCompressed;
        }

        /**
         * @return True if this image format is in floating point, 
         * false if it is an integer format.
         */
        public boolean isFloatingPont(){
            return isFloatingPoint;
        }



    }
