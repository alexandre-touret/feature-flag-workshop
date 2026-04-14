import {themes as prismThemes} from 'prism-react-renderer';
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';

const config: Config = {
  title: 'Feature-Flag Workshop',
  tagline: 'Deep dive into Feature Flagging',
  favicon: 'img/favicon.ico',

  url: 'https://blog.touret.info',
  baseUrl: '/feature-flag-workshop/',

  organizationName: 'my-org',
  projectName: 'my-codelab',
  trailingSlash: false,
  onBrokenLinks: 'throw',

  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  markdown: {
    mermaid: true,
  },
  themes: ['@docusaurus/theme-mermaid'],

  presets: [
    [
      'classic',
      {
        docs: {
          routeBasePath: '/', // Serve the docs at the site's root
          sidebarPath: './sidebars.ts',
        },
        blog: false, // Disable the blog
        theme: {
          customCss: './src/css/custom.css',
        },
      } satisfies Preset.Options,
    ],
  ],

  plugins: [
    [
      '@docusaurus/plugin-content-docs',
      {
        id: 'secondary-docs', // ID is required when running multiple instances
        path: 'secondary-docs',
        routeBasePath: 'secondary-docs',
        sidebarPath: './sidebars.ts',
      },
    ],
  ],

  themeConfig: {
    colorMode: {
      defaultMode: 'light',
      disableSwitch: false,
      respectPrefersColorScheme: true,
    },
    navbar: {
      title: 'Feature-Flag Workshop',
      logo: {
        alt: 'Code Lab Logo',
        src: 'img/logo.svg',
      },
      items: [
        {
          type: 'docSidebar',
          sidebarId: 'tutorialSidebar',
          position: 'left',
          label: 'Start Lab',
        },
      ],
    },
    footer: {
      style: 'dark',
      copyright: `Copyright © ${new Date().getFullYear()} Code Lab Project. Built with Docusaurus.`,
    },
    prism: {
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
      additionalLanguages: ['java', 'typescript', 'bash', 'json', 'yaml', 'kotlin'],
    },
  } satisfies Preset.ThemeConfig,
};

export default config;
