import type { DefaultTheme, LocaleSpecificConfig } from 'vitepress'

export const enConfig: LocaleSpecificConfig<DefaultTheme.Config> = {
  themeConfig: {
    // Only include the properties that are valid in DefaultTheme.Config
    nav: [
      { text: 'Home', link: '/' }
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
        items: [
          { text: 'Home', link: '/' },
          {
            text: 'Нow to Start',
            items: [
              { text: 'Installation', link: '/guide/installation' },
              { text: 'Compatibility', link: '/guide/compatibility' },
              { text: 'FAQ', link: '/guide/faq' }
            ]
          },
          {
            text: 'Options',
            items: [
              { text: 'Datapacks', link: '/guide/datapacks' },
              { text: 'Inventory Weights Server', link: '/guide/options/inventory_weights_server' },
              { text: 'Inventory Weights Client', link: '/guide/options/inventory_weights_client' },
              { text: 'Inventory Weights Items', link: '/guide/options/inventory_weights_items' },
            ]
          },
          {
            text: 'Features',
            items: [
              { text: 'Default Item Values', link: '/guide/features/item_default_values' },
              { text: 'Item Custom Values', link: '/guide/features/item_custom_values' },
              { text: 'Armor Pockets', link: '/guide/features/pockets' },
              { text: 'Inventory Weight', link: '/guide/features/max_weight' },
              { text: 'Overload', link: '/guide/features/overload_effect' },
              { text: 'Commands', link: '/guide/features/commands' },
              { text: 'Tooltips', link: '/guide/features/tooltips' },
              { text: 'HUD', link: '/guide/features/hud' },
            ]
          }
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
      pattern: 'https://github.com/MEGATREX4/inventory-weight/edit/docs/inventory-weight/:path',
      text: 'Suggest changes to this page',
    },
  },
}
