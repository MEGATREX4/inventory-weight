import type { DefaultTheme, LocaleSpecificConfig } from 'vitepress'
import { generateSidebar } from 'vitepress-sidebar'

export const enConfig: LocaleSpecificConfig<DefaultTheme.Config> = {
  themeConfig: {
    nav: [
      { text: 'Home', link: '/' }
    ],

    search: {
  provider: 'local',

  options: {
    translations: {
      button: {
        buttonText: 'Search',
        buttonAriaLabel: 'Search',
      },
      modal: {
        displayDetails: 'Display details',
        noResultsText: 'No results found',
        resetButtonTitle: 'Reset search',
        footer: {
          selectText: 'select',
          selectKeyAriaLabel: 'enter',
          navigateText: 'navigate',
          navigateUpKeyAriaLabel: 'up arrow',
          navigateDownKeyAriaLabel: 'down arrow',
          closeText: 'close',
          closeKeyAriaLabel: 'escape',
        },
      },
    },
  },
},

    sidebar: generateSidebar({
      documentRootPath: 'inventory-weight',

      useTitleFromFileHeading: true,
      useTitleFromFrontmatter: true,

      hyphenToSpace: true,
      underscoreToSpace: true,
      capitalizeEachWords: true,

      sortMenusByFrontmatterOrder: true,
      frontmatterOrderDefaultValue: 999,

      collapsed: false,

      excludeFolders: ['.vitepress'],
      excludeFiles: ['README.md'],
    }),

    outline: {
      label: 'On this page',
      level: 'deep',
    },

    docFooter: {
      prev: 'Previous page',
      next: 'Next page',
    },

    lastUpdated: {
      text: 'Last updated',
      formatOptions: {
        dateStyle: 'full',
        timeStyle: 'medium',
      },
    },

    editLink: {
      pattern: 'https://github.com/MEGATREX4/inventory-weight/edit/docs/inventory-weight/:path',
      text: 'Suggest changes to this page',
    },
  },
}