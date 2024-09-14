import type { DefaultTheme, LocaleSpecificConfig } from 'vitepress'

export const enConfig: LocaleSpecificConfig<DefaultTheme.Config> = {
  themeConfig: {
    // Only include the properties that are valid in DefaultTheme.Config
    nav: [
      { text: 'Home', link: '/en/' }
    ],

    search: {
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

    sidebar: [
      {
        text: 'Documentation',
        items: [
          { text: 'Home', link: '/en/' },
          { text: 'Inventory Weight', link: '/en/inventory-weight' },
          {
            text: 'Guide',
            items: [
              { text: 'Introduction', link: '/en/guide/introduction' },
            ],
          },
        ],
      },
    ],

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
      pattern: 'https://github.com/MEGATREX4/inventory-weight/edit/main/docs/:path',
      text: 'Suggest changes to this page',
    },
  },
}
