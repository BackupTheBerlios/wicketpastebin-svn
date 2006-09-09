/* No-Highlight brush is contributed by Andrew Lombardi */
dp.sh.Brushes.NoHighlight = function()
{
	var keywords =	'';

	this.regexList = [
        { regex: new RegExp('^@@.*$', 'gm'),                                       css: 'highlight' }     // highlight keyword    
            ];

	this.CssClass = 'dp-nohighlight';
}

dp.sh.Brushes.NoHighlight.prototype	= new dp.sh.Highlighter();
dp.sh.Brushes.NoHighlight.Aliases	= ['no-highlight'];
