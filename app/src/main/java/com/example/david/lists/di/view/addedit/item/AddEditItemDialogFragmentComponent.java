package com.example.david.lists.di.view.addedit.item;

import android.app.Application;

import androidx.fragment.app.Fragment;

import com.example.david.lists.di.view.addedit.AddEditDialogFragmentCommonModule;
import com.example.david.lists.di.view.common.ViewCommonModule;
import com.example.david.lists.di.view.common.ViewScope;
import com.example.david.lists.view.addedit.item.AddEditItemDialogFragment;

import javax.inject.Named;

import dagger.BindsInstance;
import dagger.Component;

import static com.example.david.lists.di.view.addedit.AddEditNamedConstants.ID;
import static com.example.david.lists.di.view.addedit.AddEditNamedConstants.TITLE;
import static com.example.david.lists.di.view.addedit.AddEditNamedConstants.USER_LIST_ID;

@ViewScope
@Component(modules = {
        AddEditItemDialogFragmentModule.class,
        AddEditDialogFragmentCommonModule.class,
        ViewCommonModule.class
})
public interface AddEditItemDialogFragmentComponent {
    void inject(AddEditItemDialogFragment fragment);

    @Component.Builder
    interface Builder {
        AddEditItemDialogFragmentComponent build();

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder fragment(Fragment fragment);

        @BindsInstance
        Builder id(@Named(ID) String id);

        @BindsInstance
        Builder title(@Named(TITLE) String title);

        @BindsInstance
        Builder userListId(@Named(USER_LIST_ID) String userListId);
    }
}