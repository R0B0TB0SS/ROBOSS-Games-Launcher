package be.R0B0TB0SS.launcher.authentification.model.response;

public class MinecraftProfile
    {
        private final String id;
        private final String name;
        private final MinecraftProfile.MinecraftSkin[] skins;

    public MinecraftProfile(String id, String name, MinecraftProfile.MinecraftSkin[] skins)
        {
            this.id = id;
            this.name = name;
            this.skins = skins;
        }

        /**
         * @return The player Minecraft UUID
         */
        public String getId()
        {
            return id;
        }

        /**
         * @return The player Minecraft username
         */
        public String getName()
        {
            return name;
        }

        public MinecraftProfile.MinecraftSkin[] getSkins()
        {
            return skins;
        }

        public static class MinecraftSkin
        {
            private final String id;
            private final String state;
            private final String url;
            private final String variant;
            private final String alias;

            public MinecraftSkin(String id, String state, String url, String variant, String alias)
            {
                this.id = id;
                this.state = state;
                this.url = url;
                this.variant = variant;
                this.alias = alias;
            }

            public String getId()
            {
                return id;
            }

            public String getState()
            {
                return state;
            }

            public String getUrl()
            {
                return url;
            }

            public String getVariant()
            {
                return variant;
            }

            public String getAlias()
            {
                return alias;
            }
        }
    }

